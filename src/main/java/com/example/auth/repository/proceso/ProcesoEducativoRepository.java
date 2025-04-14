package com.example.auth.repository.proceso;

import com.example.auth.models.proceso.ProcesoEducativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcesoEducativoRepository extends JpaRepository<ProcesoEducativo, Long> {
} 