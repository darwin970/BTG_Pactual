package com.btg.core.dominio.fondo.modelo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FondoDTO {

    private String id;
    private String nombre;
    private Double montoMinimo;
    private String categoria;
}
