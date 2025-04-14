package com.example.auth.controllers;

import com.example.auth.models.ERole;
import com.example.auth.models.Role;
import com.example.auth.models.User;
import com.example.auth.payload.request.UpdateUserRequest;
import com.example.auth.payload.response.MessageResponse;
import com.example.auth.payload.response.UserResponse;
import com.example.auth.repository.RoleRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.util.PasswordUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Base64;

@SuppressWarnings("unused")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_RECTOR')")
    public ResponseEntity<?> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/docentes")
    @PreAuthorize("hasRole('ROLE_RECTOR')")
    public ResponseEntity<?> getDocentes() {
        Optional<Role> docenteRole = roleRepository.findByName(ERole.ROLE_DOCENTE);
        if (docenteRole.isPresent()) {
            List<UserResponse> docentes = userRepository.findAll().stream()
                    .filter(user -> user.getRoles().contains(docenteRole.get()))
                    .map(UserResponse::fromUser)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(docentes);
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/rectores")
    @PreAuthorize("hasRole('ROLE_RECTOR')")
    public ResponseEntity<?> getRectores() {
        Optional<Role> rectorRole = roleRepository.findByName(ERole.ROLE_RECTOR);
        if (rectorRole.isPresent()) {
            List<UserResponse> rectores = userRepository.findAll().stream()
                    .filter(user -> user.getRoles().contains(rectorRole.get()))
                    .map(UserResponse::fromUser)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(rectores);
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RECTOR')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(UserResponse::fromUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RECTOR')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok(new MessageResponse("Usuario eliminado con éxito"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RECTOR')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateRequest) {
        return userRepository.findById(id)
                .map(user -> {
                    // Actualizar username si se proporciona
                    if (updateRequest.getUsername() != null && !updateRequest.getUsername().isEmpty()) {
                        // Comprobar si el username ya existe y no es el mismo usuario
                        if (userRepository.existsByUsername(updateRequest.getUsername()) && 
                            !user.getUsername().equals(updateRequest.getUsername())) {
                            return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("Error: El nombre de usuario ya está en uso!"));
                        }
                        user.setUsername(updateRequest.getUsername());
                    }
                    
                    // Actualizar password si se proporciona
                    if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
                        // Procesar la contraseña (puede estar en texto plano o Base64)
                        String processedPassword = PasswordUtil.processPassword(updateRequest.getPassword());
                        user.setPassword(encoder.encode(processedPassword));
                    }
                    
                    // Actualizar roles si se proporcionan
                    if (updateRequest.getRoles() != null && !updateRequest.getRoles().isEmpty()) {
                        Set<Role> roles = new HashSet<>();
                        
                        updateRequest.getRoles().forEach(role -> {
                            switch (role) {
                                case "rector":
                                    Role rectorRole = roleRepository.findByName(ERole.ROLE_RECTOR)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                                    roles.add(rectorRole);
                                    break;
                                default:
                                    Role docenteRole = roleRepository.findByName(ERole.ROLE_DOCENTE)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                                    roles.add(docenteRole);
                            }
                        });
                        
                        user.setRoles(roles);
                    }
                    
                    userRepository.save(user);
                    return ResponseEntity.ok(UserResponse.fromUser(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 