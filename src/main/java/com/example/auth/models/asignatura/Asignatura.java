package com.example.auth.models.asignatura;

import com.example.auth.models.area.Area;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Table(name = "asignatura")
@NoArgsConstructor
@AllArgsConstructor
public class Asignatura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asi_id")
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(name = "asi_nombre")
    private String nombre;

    @Size(max = 50)
    @Column(name = "asi_nemonico", unique = true)
    private String nemonico;

    @Column(name = "asi_estado")
    private String estado = "ACTIVO";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ar_id")
    private Area area;
} 