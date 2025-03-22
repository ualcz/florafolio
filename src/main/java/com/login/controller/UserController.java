package com.login.controller;

import com.login.dto.LoginRequestDTO;
import com.login.dto.LoginResponseDTO;
import com.login.dto.PasswordUpdateDTO;
import com.login.dto.RegisterRequestDTO;
import com.login.dto.ResponseDTO;
import com.login.dto.UserDTO;
import com.login.dto.UsernameUpdateDTO;
import com.login.model.User;
import com.login.service.UserService;
import com.login.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // Rate limiting implementation - in production, use a distributed cache like Redis
    private final Map<String, AtomicInteger> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, Long> blockedIps = new ConcurrentHashMap<>();
    
    // Rate limiting constants
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_MS = 15 * 60 * 1000; // 15 minutes
    

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO loginRequest, 
            HttpServletRequest request) {
        
        // Get client IP for rate limiting
        String clientIp = getClientIp(request);
        
        // Check if IP is blocked
        if (isIpBlocked(clientIp)) {
            LoginResponseDTO response = new LoginResponseDTO(
                "error", 
                "Too many failed login attempts. Please try again later.", 
                null, 
                null
            );
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
        }
        
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User user = userService.authenticate(username, password);
        
        if (user != null) {
            // Reset login attempts on successful login
            resetLoginAttempts(clientIp);
            
            String token = jwtUtil.generateToken(user);
            LoginResponseDTO response = new LoginResponseDTO(
                "success", 
                "Login bem-sucedido", 
                user.getUsername(), 
                token
            );
            return ResponseEntity.ok(response);
        } else {
            // Increment failed login attempts
            incrementLoginAttempts(clientIp);
            
            LoginResponseDTO response = new LoginResponseDTO(
                "error", 
                "Credenciais inválidas", 
                null, 
                null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        String email = registerRequest.getEmail();

        if (username == null || password == null || email == null) {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Username, password e email são obrigatórios"
            );
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User(null, username, password, email);
        System.out.println("Senha recebida: " + user.getPassword());
        User registeredUser = userService.registerUser(user);
        
        if (registeredUser != null) {
            ResponseDTO response = new ResponseDTO(
                "success", 
                "Usuário registrado com sucesso"
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Nome de usuário já existe"
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

 
    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            jwtUtil.revokeToken(token);
            ResponseDTO response = new ResponseDTO(
                "success", 
                "Logout realizado com sucesso"
            );
            return ResponseEntity.ok(response);
        } else {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Token inválido"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    

    @GetMapping("/users/profile")
    public ResponseEntity<?> getCurrentUserProfile(
            @RequestHeader(value = "Authorization", required = true) String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Token de autenticação necessário"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        String token = authHeader.substring(7);
        
        try {
            // Extract user ID from token
            UUID userId = jwtUtil.extractUserId(token);
            
            // Get user by ID
            User user = userService.getUserById(userId);
            
            if (user != null) {
                // Create DTO with full profile access since it's the user's own profile
                UserDTO userDTO = new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    true // isOwnProfile is always true here
                );
                return ResponseEntity.ok(userDTO);
            } else {
                ResponseDTO response = new ResponseDTO(
                    "error", 
                    "Usuário não encontrado"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Token inválido ou expirado"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    

 
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserByUsername(
            @PathVariable String username,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        User user = userService.getUserByUsername(username);
        if (user != null) {
            // Check if the requester is the same user (to determine if email should be visible)
            boolean isOwnProfile = false;
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    UUID tokenUserId = jwtUtil.extractUserId(token);
                    isOwnProfile = user.getId().equals(tokenUserId);
                } catch (Exception e) {
                    // Token parsing failed, assume not own profile
                }
            }

            UserDTO userDTO;
            if (isOwnProfile) {
                userDTO = new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    true
                );
            } else {
                userDTO = new UserDTO(
                    user.getUsername(),
                    false
            );    
            }
            return ResponseEntity.ok(userDTO);
           
        } else {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Usuário não encontrado"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/users/{id}/username")
    public ResponseEntity<?> updateUsernameById(
            @PathVariable UUID id,
            @RequestBody UsernameUpdateDTO usernameUpdateDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String currentUsername = usernameUpdateDTO.getCurrentUsername();
        String newUsername = usernameUpdateDTO.getNewUsername();
        
        // Verify required fields
        if (currentUsername == null || newUsername == null) {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Campos currentUsername e newUsername obrigatórios"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Update username
        boolean updated = userService.updateUsernameById(id, newUsername);
        if (updated) {
            // If there's a token, revoke it since username has changed
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                jwtUtil.revokeToken(token);
                
                // Generate a new token with updated username
                User user = userService.getUserById(id);
                String newToken = jwtUtil.generateToken(user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Usuário atualizado com sucesso");
                response.put("token", newToken);
                
                return ResponseEntity.ok(response);
            } else {
                ResponseDTO response = new ResponseDTO(
                    "success", 
                    "Usuário atualizado com sucesso"
                );
                return ResponseEntity.ok(response);
            }
        } else {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Usuário nao encontrado"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    

    @PutMapping("/users/{id}/password")
    public ResponseEntity<?> updatePasswordById(
            @PathVariable UUID id,
            @RequestBody PasswordUpdateDTO passwordUpdateDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // Extract request data
        String currentPassword = passwordUpdateDTO.getCurrentPassword();
        String newPassword = passwordUpdateDTO.getNewPassword();
        
        // Verify required fields
        if (currentPassword == null || newPassword == null) {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Senha atual e nova senha são obrigatórias"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Get user by ID
        User user = userService.getUserById(id);
        if (user == null) {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Usuário não encontrado"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        // Verify current password
        if (!userService.getPasswordEncoder().matches(currentPassword, user.getPassword())) {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Senha atual incorreta"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // Update password
        boolean updated = userService.updatePasswordById(id, newPassword);
        if (updated) {
            // If there's a token, revoke it since password has changed
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                jwtUtil.revokeToken(token);
                
                // Generate a new token
                String newToken = jwtUtil.generateToken(user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Senha atualizada com sucesso");
                response.put("token", newToken);
                
                return ResponseEntity.ok(response);
            } else {
                ResponseDTO response = new ResponseDTO(
                    "success", 
                    "Senha atualizada com sucesso"
                );
                return ResponseEntity.ok(response);
            }
        } else {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Não foi possível atualizar a senha"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    private boolean isIpBlocked(String ip) {
        Long blockedUntil = blockedIps.get(ip);
        if (blockedUntil != null) {
            if (System.currentTimeMillis() < blockedUntil) {
                return true;
            } else {
                // Block duration expired, remove from blocked IPs
                blockedIps.remove(ip);
                loginAttempts.remove(ip);
                return false;
            }
        }
        return false;
    }
    

    private void incrementLoginAttempts(String ip) {
        AtomicInteger attempts = loginAttempts.computeIfAbsent(ip, k -> new AtomicInteger(0));
        int currentAttempts = attempts.incrementAndGet();
        
        if (currentAttempts >= MAX_ATTEMPTS) {
            // Block the IP
            blockedIps.put(ip, System.currentTimeMillis() + BLOCK_DURATION_MS);
        }
    }
    

    private void resetLoginAttempts(String ip) {
        loginAttempts.remove(ip);
        blockedIps.remove(ip);
    }
}