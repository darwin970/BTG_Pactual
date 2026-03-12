package com.btg.core.dominio.notificacion.modelo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionDTO {

    private String id;
    private String clienteId;
    private String canal;
    private String destinatario;
    private String mensaje;
    private String estado;
    private String detalleError;
    private String timestamp;
}
