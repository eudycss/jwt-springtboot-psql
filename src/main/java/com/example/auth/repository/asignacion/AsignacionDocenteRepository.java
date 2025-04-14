package com.example.auth.repository.asignacion;

import com.example.auth.models.asignacion.AsignacionDocente;
import com.example.auth.models.oferta.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionDocenteRepository extends JpaRepository<AsignacionDocente, Long> {
    List<AsignacionDocente> findByOfertaIn(List<Oferta> ofertas);
} 