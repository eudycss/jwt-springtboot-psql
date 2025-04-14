package com.example.auth.repository.oferta;

import com.example.auth.models.oferta.Oferta;
import com.example.auth.models.proceso.ProcesoEducativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Long> {
    List<Oferta> findByGradoProcesoEducativoAndPeriodoLectivoId(ProcesoEducativo procesoEducativo, Long periodoLectivoId);
} 