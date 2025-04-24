package com.florafolio.service;

import com.florafolio.model.User;
import com.florafolio.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
    
    @PostConstruct
    @Transactional
    public void init() {
        System.out.println("Iniciando verificação de usuários no banco de dados...");
        try {
            if (userRepository.count() == 0) {
                System.out.println("Banco de dados vazio. Criando usuários iniciais...");
                // Cria o usuário administrador
                User admin = new User(null, "admin", passwordEncoder.encode("admin123"), "admin@example.com", User.Role.ADMIN);
                admin = userRepository.save(admin);
                System.out.println("Usuário admin criado com sucesso. ID: " + admin.getId());

                // Cria o usuário padrão
                User user = new User(null, "user", passwordEncoder.encode("user123"), "user@example.com", User.Role.USER);
                user = userRepository.save(user);
                System.out.println("Usuário padrão criado com sucesso. ID: " + user.getId());
            } else {
                System.out.println("Usuários já existem no banco de dados. Verificando admin...");
                User admin = userRepository.findByUsername("admin");
                if (admin == null) {
                    System.out.println("Usuário admin não encontrado. Criando...");
                    admin = new User(null, "admin", passwordEncoder.encode("admin123"), "admin@example.com", User.Role.ADMIN);
                    admin = userRepository.save(admin);
                    System.out.println("Usuário admin criado com sucesso. ID: " + admin.getId());
                } else {
                    System.out.println("Usuário admin já existe. ID: " + admin.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro durante a inicialização dos usuários: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public User authenticate(String username, String password) {
        System.out.println("Tentando autenticar usuário: " + username);
        User user = userRepository.findByUsername(username);
        if (user != null) {
            System.out.println("Usuário encontrado: " + user.getUsername());
            System.out.println("Senha armazenada: " + user.getPassword());
            System.out.println("Senha fornecida: " + password);
            if (passwordEncoder.matches(password, user.getPassword())) {
                System.out.println("Senha válida para usuário: " + username);
                return user;
            } else {
                System.out.println("Senha inválida para usuário: " + username);
            }
        } else {
            System.out.println("Usuário não encontrado: " + username);
        }
        return null;
    }
    
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return null; // Usuário já existe
        }
        System.out.println("Senha antes da criptografia: " + user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("Senha após criptografia: " + user.getPassword());
        return userRepository.save(user);
    }

    

    @Transactional
    public User deleteUserById(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userRepository.delete(user);
            return user;
        }
        return null;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean updatePasswordById(UUID id, String newPassword) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateUsernameById(UUID id, String newUsername) {
        if (userRepository.existsByUsername(newUsername)) {
            return false; // Novo nome de usuário já existe
        }
        
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(newUsername);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}