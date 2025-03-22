package com.login.controller;

import com.login.model.User;
import com.login.service.UserService;

import jakarta.validation.Valid;

import com.login.security.JwtUtil;
import com.login.dto.UserDTO;
import com.login.dto.LoginRequestDTO;
import com.login.dto.LoginResponseDTO;
import com.login.dto.RegisterRequestDTO;
import com.login.dto.PasswordUpdateDTO;
import com.login.dto.ResponseDTO;
import com.login.dto.UsernameUpdateDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class LoginController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login( @Valid @RequestBody LoginRequestDTO loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User user = userService.authenticate(username, password);
        
        if (user != null) {
            String token = jwtUtil.generateToken(user);
            LoginResponseDTO response = new LoginResponseDTO(
                "success", 
                "Login bem-sucedido", 
                user.getId(), 
                user.getUsername(), 
                token
            );
            return ResponseEntity.ok(response);
        } else {
            LoginResponseDTO response = new LoginResponseDTO(
                "error", 
                "Credenciais inválidas", 
                null, 
                null, 
                null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register( @Valid @RequestBody RegisterRequestDTO registerRequest) {
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
    
    @PutMapping("/users/id/{id}/username")
    public ResponseEntity<ResponseDTO> updateUsernameById(
            @PathVariable UUID id,
            @RequestBody UsernameUpdateDTO usernameUpdateDTO) {
        String currentUsername = usernameUpdateDTO.getCurrentUsername();
        String newUsername = usernameUpdateDTO.getNewUsername();
        
        // Verificar se os campos necessários está presentes
        if (currentUsername == null || newUsername == null) {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Campos currentUsername e newUsername obrigatórios"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        boolean updated = userService.updateUsernameById(id, newUsername);
        if (updated) {
            ResponseDTO response = new ResponseDTO(
                "success", 
                "Usuário atualizado com sucesso"
            );
            return ResponseEntity.ok(response);
        } else {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Usuário nao encontrado"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user != null) {
            UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
            );
            return ResponseEntity.ok(userDTO);
        } else {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Usuário não encontrado"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PutMapping("/users/id/{id}/password")
    public ResponseEntity<ResponseDTO> updatePasswordById(
            @PathVariable UUID id,
            @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        
        // Extrair dados da requisição
        String currentPassword = passwordUpdateDTO.getCurrentPassword();
        String newPassword = passwordUpdateDTO.getNewPassword();
        
        // Verificar se os campos necessários estão presentes
        if (currentPassword == null || newPassword == null) {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Senha atual e nova senha são obrigatórias"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Buscar o usuário pelo ID
        User user = userService.getUserById(id);
        if (user == null) {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Usuário não encontrado"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        // Verificar se a senha atual é válida usando o passwordEncoder
        if (!userService.getPasswordEncoder().matches(currentPassword, user.getPassword())) {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Senha atual incorreta"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // Atualizar a senha
        boolean updated = userService.updatePasswordById(id, newPassword);
        if (updated) {
            ResponseDTO response = new ResponseDTO(
                "success", 
                "Senha atualizada com sucesso"
            );
            return ResponseEntity.ok(response);
        } else {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Não foi possível atualizar a senha"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/users/id/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        if (user != null) {
            UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
            );
            return ResponseEntity.ok(userDTO);
        } else {
            ResponseDTO response = new ResponseDTO(
                "error", 
                "Usuário não encontrado"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
}