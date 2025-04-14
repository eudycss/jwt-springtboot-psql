package com.example.auth.models.area;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Table(name = "area")
@NoArgsConstructor
@AllArgsConstructor
public class Area {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ar_id")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "ar_nombre")
    private String nombre;

    @Column(name = "ar_estado")
    private String estado = "ACTIVO";
} 