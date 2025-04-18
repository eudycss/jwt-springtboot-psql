package com.example.auth.controllers;

import com.example.auth.models.ERole;
import com.example.auth.models.Role;
import com.example.auth.models.User;
import com.example.auth.payload.request.LoginRequest;
import com.example.auth.payload.request.SignupRequest;
import com.example.auth.payload.response.JwtResponse;
import com.example.auth.payload.response.MessageResponse;
import com.example.auth.repository.RoleRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.jwt.JwtUtils;
import com.example.auth.security.services.UserDetailsImpl;
import com.example.auth.util.PasswordUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Procesar la contraseña (puede estar en texto plano o Base64)
        String processedPassword = PasswordUtil.processPassword(loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), processedPassword));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();        
        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, 
                                                 userDetails.getId(), 
                                                 userDetails.getUsername(), 
                                                 roles));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: El nombre de usuario ya está en uso!"));
        }

        // Procesar la contraseña (puede estar en texto plano o Base64)
        String processedPassword = PasswordUtil.processPassword(signUpRequest.getPassword());

        // Crear la cuenta de usuario con la contraseña procesada
        User user = new User(signUpRequest.getUsername(), 
                             encoder.encode(processedPassword));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role docenteRole = roleRepository.findByName(ERole.ROLE_DOCENTE)
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
            roles.add(docenteRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "rector":
                        Role rectorRole = roleRepository.findByName(ERole.ROLE_RECTOR)
                            .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(rectorRole);
                        break;
                    default:
                        Role docenteRole = roleRepository.findByName(ERole.ROLE_DOCENTE)
                            .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(docenteRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
    }
} 