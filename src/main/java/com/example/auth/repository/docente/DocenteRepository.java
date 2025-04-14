package com.example.auth.repository.docente;

import com.example.auth.models.docente.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
    Optional<Docente> findByCedula(String cedula);
    List<Docente> findByActivoTrue();
    boolean existsByCedula(String cedula);
} 