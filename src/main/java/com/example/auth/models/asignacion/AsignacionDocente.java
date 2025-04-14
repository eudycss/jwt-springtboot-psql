package com.example.auth.models.asignacion;

import com.example.auth.models.docente.Docente;
import com.example.auth.models.oferta.Oferta;
import com.example.auth.models.asignatura.Asignatura;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@Table(name = "asignacion_docente")
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionDocente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asdo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_id", nullable = false)
    private Docente docente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ofe_id", nullable = false)
    private Oferta oferta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asi_id", nullable = false)
    private Asignatura asignatura;

    @Column(name = "asdo_estado")
    private Boolean estado = true;
} 