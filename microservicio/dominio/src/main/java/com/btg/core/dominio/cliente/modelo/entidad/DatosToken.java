package com.btg.core.dominio.cliente.modelo.entidad;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DatosToken {

    private final String clienteId;
    private final String email;
    private final String rol;
}
