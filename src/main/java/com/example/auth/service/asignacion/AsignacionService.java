package com.example.auth.service.asignacion;

import com.example.auth.models.asignacion.AsignacionDocente;
import com.example.auth.models.proceso.ProcesoEducativo;
import com.example.auth.models.oferta.Oferta;
import com.example.auth.models.asignatura.Asignatura;
import com.example.auth.payload.response.OfertaAsignacionDto;
import com.example.auth.repository.asignacion.AsignacionDocenteRepository;
import com.example.auth.repository.proceso.ProcesoEducativoRepository;
import com.example.auth.repository.oferta.OfertaRepository;
import com.example.auth.repository.asignatura.AsignaturaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AsignacionService {

    @Autowired
    private ProcesoEducativoRepository procesoEducativoRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    @Autowired
    private AsignacionDocenteRepository asignacionDocenteRepository;

    @Transactional(readOnly = true)
    public List<OfertaAsignacionDto> getOfertasPorProceso(Long procesoId, Long periodoId) {
        ProcesoEducativo proceso = procesoEducativoRepository.findById(procesoId)
            .orElseThrow(() -> new EntityNotFoundException("Proceso Educativo no encontrado"));

        // Obtener todas las ofertas del proceso y periodo
        List<Oferta> ofertas = ofertaRepository.findByGradoProcesoEducativoAndPeriodoLectivoId(proceso, periodoId);

        // Obtener todas las asignaciones existentes para estas ofertas
        Set<String> asignacionesExistentes = asignacionDocenteRepository.findByOfertaIn(ofertas)
            .stream()
            .map(ad -> ad.getOferta().getId() + "-" + ad.getAsignatura().getId())
            .collect(Collectors.toSet());

        // Obtener las asignaturas según el proceso educativo
        List<Asignatura> asignaturasAplicables = getAsignaturasPorProceso(proceso.getNemonico());

        return ofertas.stream()
            .map(oferta -> mapToOfertaAsignacionDto(oferta, asignaturasAplicables, asignacionesExistentes))
            .collect(Collectors.toList());
    }

    private List<Asignatura> getAsignaturasPorProceso(String procesoNemonico) {
        // Implementar lógica según las reglas de cada proceso
        switch (procesoNemonico) {
            case "IPS":
                return asignaturaRepository.findByNemonicoIn(List.of("UNIDINT", "PROESC"));
            case "FCAP":
            case "DDTE":
                return asignaturaRepository.findByNemonicoIn(List.of("UNIDINT", "PROESC", "ING"));
            case "PAI":
                return asignaturaRepository.findAll(); // Todas las asignaturas
            default:
                throw new IllegalArgumentException("Proceso educativo no válido: " + procesoNemonico);
        }
    }

    private OfertaAsignacionDto mapToOfertaAsignacionDto(
            Oferta oferta, 
            List<Asignatura> asignaturasAplicables,
            Set<String> asignacionesExistentes) {
        
        OfertaAsignacionDto dto = new OfertaAsignacionDto();
        dto.setOfeId(oferta.getId());
        dto.setCurso(oferta.getCurso());
        dto.setParalelo(oferta.getParalelo());
        dto.setAforo(oferta.getAforo());

        OfertaAsignacionDto.GradoDto gradoDto = new OfertaAsignacionDto.GradoDto();
        gradoDto.setGraId(oferta.getGrado().getId());
        gradoDto.setDescripcion(oferta.getGrado().getDescripcion());
        gradoDto.setNemonico(oferta.getGrado().getNemonico());
        dto.setGrado(gradoDto);

        dto.setAsignaturas(asignaturasAplicables.stream()
            .map(asignatura -> {
                OfertaAsignacionDto.AsignaturaOfertaDto asignaturaDto = 
                    new OfertaAsignacionDto.AsignaturaOfertaDto();
                asignaturaDto.setAsiId(asignatura.getId());
                asignaturaDto.setNombre(asignatura.getNombre());
                asignaturaDto.setNemonico(asignatura.getNemonico());
                asignaturaDto.setAsignado(
                    asignacionesExistentes.contains(oferta.getId() + "-" + asignatura.getId())
                );
                return asignaturaDto;
            })
            .collect(Collectors.toList()));

        return dto;
    }
} 