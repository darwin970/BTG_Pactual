package com.btg.core.dominio.cliente.modelo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {

    private String id;
    private String nombre;
    private String email;
    private String telefono;
    private Double saldo;
    private String rol;
}
