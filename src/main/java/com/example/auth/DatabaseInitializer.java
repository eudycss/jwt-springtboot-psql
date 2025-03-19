package com.example.auth;

import com.example.auth.models.ERole;
import com.example.auth.models.Role;
import com.example.auth.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        // Inicializar roles
        if (roleRepository.count() == 0) {
            // Crear roles
            Role roleDocente = new Role(ERole.ROLE_DOCENTE);
            Role roleRector = new Role(ERole.ROLE_RECTOR);

            // Guardar roles
            roleRepository.save(roleDocente);
            roleRepository.save(roleRector);
            
            System.out.println("Roles inicializados en la base de datos");
        }
    }
} 