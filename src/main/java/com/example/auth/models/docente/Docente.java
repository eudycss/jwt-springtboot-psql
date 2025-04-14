package com.example.auth.models.docente;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Table(name = "docentes")
@NoArgsConstructor
@AllArgsConstructor
public class Docente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String nombres;

    @NotBlank
    @Size(max = 100)
    private String apellidos;

    @NotBlank
    @Size(max = 10)
    @Column(unique = true)
    private String cedula;

    @Size(max = 100)
    private String especialidad;

    @Column(name = "fecha_ingreso")
    private java.time.LocalDate fechaIngreso;

    private Boolean activo = true;
} 