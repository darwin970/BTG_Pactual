package com.btg.core.dominio.transaccion.modelo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransaccionDTO {

    private String id;
    private String fondoId;
    private String nombreFondo;
    private String clienteId;
    private Double monto;
    private String estado;
    private String preferenciaNotificacion;
    private String fecha;
    private String fechaCancelacion;
}
