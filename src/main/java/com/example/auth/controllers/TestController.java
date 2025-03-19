package com.example.auth.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class TestController {
    
    @GetMapping("/all")
    public String allAccess() {
        return "Contenido público.";
    }
    
    @GetMapping("/docente")
    //@PreAuthorize("hasRole('ROLE_DOCENTE') or hasRole('ROLE_RECTOR')")
    @PreAuthorize("hasRole('ROLE_DOCENTE')")
    public String docenteAccess() {
        return "Contenido para DOCENTES";
    }

    @GetMapping("/rector")
    @PreAuthorize("hasRole('ROLE_RECTOR')")
    public String rectorAccess() {
        return "Contenido solo para RECTORES.";
    }
} 
