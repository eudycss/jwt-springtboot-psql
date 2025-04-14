package com.example.auth.models.grado;

import com.example.auth.models.proceso.ProcesoEducativo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Table(name = "grado")
@NoArgsConstructor
@AllArgsConstructor
public class Grado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gra_id")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "gra_descripcion", nullable = false)
    private String descripcion;

    @Column(name = "gra_estado")
    private String estado = "ACTIVO";

    @Size(max = 50)
    @Column(name = "gra_nemonico", unique = true)
    private String nemonico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pred_id", nullable = false)
    private ProcesoEducativo procesoEducativo;
} 