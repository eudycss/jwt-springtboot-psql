package com.example.auth.repository.asignatura;

import com.example.auth.models.asignatura.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {
    List<Asignatura> findByNemonicoIn(List<String> nemonicos);
} 