package com.example.auth.payload.response;

import lombok.Data;
import java.util.List;

@Data
public class OfertaAsignacionDto {
    private Long ofeId;
    private String curso;
    private String paralelo;
    private Integer aforo;
    private GradoDto grado;
    private List<AsignaturaOfertaDto> asignaturas;

    @Data
    public static class GradoDto {
        private Long graId;
        private String descripcion;
        private String nemonico;
    }

    @Data
    public static class AsignaturaOfertaDto {
        private Long asiId;
        private String nombre;
        private String nemonico;
        private boolean asignado;
    }
} 