package com.example.auth.models.periodo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "periodo_lectivo")
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoLectivo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pele_id")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "pele_nombre")
    private String nombre;

    @NotNull
    @Column(name = "pele_fecha_inicio")
    private LocalDate fechaInicio;

    @NotNull
    @Column(name = "pele_fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "pele_activo")
    private Boolean activo = true;

    @Column(name = "pele_estado")
    private Boolean estado = true;
} 