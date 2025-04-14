package com.example.auth.models.proceso;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Table(name = "proceso_educativo")
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoEducativo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pred_id")
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "pred_descripcion", nullable = false)
    private String descripcion;

    @Size(max = 50)
    @Column(name = "pred_nemonico", unique = true)
    private String nemonico;

    @Column(name = "pred_estado")
    private String estado = "ACTIVO";

    @Size(max = 50)
    @Column(name = "pred_nivel")
    private String nivel;
} 