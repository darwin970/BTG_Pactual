package com.btg.core.dominio.fondo.modelo.entidad;

import com.btg.core.dominio.ValidadorArgumento;
import lombok.Getter;

@Getter
public class Fondo {

    private final String id;
    private final String nombre;
    private final Double montoMinimo;
    private final String categoria;

    public Fondo(String id, String nombre, Double montoMinimo, String categoria) {
        ValidadorArgumento.validarObligatorio(id, "El id del fondo es obligatorio");
        ValidadorArgumento.validarObligatorio(nombre, "El nombre del fondo es obligatorio");
        ValidadorArgumento.validarObligatorio(montoMinimo, "El monto mínimo del fondo es obligatorio");
        ValidadorArgumento.validarPositivo(montoMinimo, "El monto mínimo del fondo debe ser positivo");
        ValidadorArgumento.validarObligatorio(categoria, "La categoría del fondo es obligatoria");

        this.id = id;
        this.nombre = nombre;
        this.montoMinimo = montoMinimo;
        this.categoria = categoria;
    }
}
