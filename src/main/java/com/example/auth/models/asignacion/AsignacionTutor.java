package com.example.auth.models.asignacion;

import com.example.auth.models.docente.Docente;
import com.example.auth.models.oferta.Oferta;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Table(name = "asignacion_tutor")
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionTutor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "astu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_id", nullable = false)
    private Docente docente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ofe_id", nullable = false)
    private Oferta oferta;

    @Column(name = "astu_estado")
    private Boolean estado = true;
} 