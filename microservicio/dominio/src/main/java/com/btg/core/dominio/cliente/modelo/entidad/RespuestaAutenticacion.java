package com.btg.core.dominio.cliente.modelo.entidad;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RespuestaAutenticacion {

    private final String token;
    private final long expiracion;
}
