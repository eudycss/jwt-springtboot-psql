package com.example.auth.models.oferta;

import com.example.auth.models.grado.Grado;
import com.example.auth.models.periodo.PeriodoLectivo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Table(name = "oferta")
@NoArgsConstructor
@AllArgsConstructor
public class Oferta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ofe_id")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "ofe_curso")
    private String curso;

    @NotBlank
    @Size(max = 10)
    @Column(name = "ofe_paralelo")
    private String paralelo;

    @Column(name = "ofe_aforo")
    private Integer aforo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gra_id", nullable = false)
    private Grado grado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pele_id", nullable = false)
    private PeriodoLectivo periodoLectivo;

    @Column(name = "ofe_estado")
    private Boolean estado = true;
} 