package com.login.controller;

import com.login.model.User;
import com.login.service.UserService;
import com.login.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

@RestController
public class LoginController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        Map<String, Object> response = new HashMap<>();

        User user = userService.authenticate(username, password);
        
        if (user != null) {
            String token = jwtUtil.generateToken(user);
            response.put("status", "success");
            response.put("message", "Login bem-sucedido");
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Credenciais inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Logout bem-sucedido");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        String email = credentials.get("email");
        Map<String, Object> response = new HashMap<>();

        if (username == null || password == null || email == null) {
            response.put("status", "error");
            response.put("message", "Username, password e email são obrigatórios");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User(null, username, password, email);
        System.out.println("Senha recebida: " + user.getPassword());
        User registeredUser = userService.registerUser(user);
        Map<String, Object> registerResponse = new HashMap<>();
        
        if (registeredUser != null) {
            registerResponse.put("status", "success");
            registerResponse.put("message", "Usuário registrado com sucesso");
            registerResponse.put("user", user.getUsername());
            registerResponse.put("id", user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
        } else {
            registerResponse.put("status", "error");
            registerResponse.put("message", "Nome de usuário já existe");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(registerResponse);
        }
    }
    
    @PutMapping("/users/id/{id}/username")
    public ResponseEntity<Map<String, Object>> updateUsernameById(
            @PathVariable UUID id,
            @RequestBody Map<String, String> usernameData) {
        String currentUsername = usernameData.get("currentUsername");
        String newUsername = usernameData.get("newUsername");
        Map<String, Object> response = new HashMap<>();
        
        // Verificar se os campos necessários está presentes
        if (currentUsername == null || newUsername == null) {
            response.put("status", "error");
            response.put("message", "Campos currentUsername e newUsername obrigatórios");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        boolean updated = userService.updateUsernameById(id, newUsername);
        if (updated) {
            response.put("status", "success");
            response.put("message", "Usuário atualizado com sucesso");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Usuário nao encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PutMapping("/users/id/{id}/password")
    public ResponseEntity<Map<String, Object>> updatePasswordById(
            @PathVariable UUID id,
            @RequestBody Map<String, String> passwordData) {
        
        // Extrair dados da requisição
        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");
        Map<String, Object> response = new HashMap<>();
        
        // Verificar se os campos necessários estão presentes
        if (currentPassword == null || newPassword == null) {
            response.put("status", "error");
            response.put("message", "Senha atual e nova senha são obrigatórias");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Buscar o usuário pelo ID
        User user = userService.getUserById(id);
        if (user == null) {
            response.put("status", "error");
            response.put("message", "Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        // Verificar se a senha atual é válida usando o passwordEncoder
        if (!userService.getPasswordEncoder().matches(currentPassword, user.getPassword())) {
            response.put("status", "error");
            response.put("message", "Senha atual incorreta");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // Atualizar a senha
        boolean updated = userService.updatePasswordById(id, newPassword);
        if (updated) {
            response.put("status", "success");
            response.put("message", "Senha atualizada com sucesso");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Não foi possível atualizar a senha");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/users/id/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        Map<String, Object> response = new HashMap<>();
        if (user != null) {
            response.put("status", "success");
            response.put("message", "Usuário encontrado");
            response.put("user", user.getUsername());
            response.put("id", user.getEmail());
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
}