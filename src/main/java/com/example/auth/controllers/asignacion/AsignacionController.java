package com.example.auth.controllers.asignacion;

import com.example.auth.payload.response.OfertaAsignacionDto;
import com.example.auth.service.asignacion.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/asignacion")
@PreAuthorize("hasRole('ROLE_RECTOR')")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    @GetMapping("/ofertas-por-proceso/{procesoId}")
    public ResponseEntity<List<OfertaAsignacionDto>> getOfertasPorProceso(
            @PathVariable Long procesoId,
            @RequestParam Long periodoId) {
        return ResponseEntity.ok(asignacionService.getOfertasPorProceso(procesoId, periodoId));
    }
} 