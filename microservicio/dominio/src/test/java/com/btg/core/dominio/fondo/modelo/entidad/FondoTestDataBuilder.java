package com.btg.core.dominio.fondo.modelo.entidad;

public class FondoTestDataBuilder {

    private String id;
    private String nombre;
    private Double montoMinimo;
    private String categoria;

    public FondoTestDataBuilder() {
        this.id = "1";
        this.nombre = "FPV_BTG_PACTUAL_RECAUDADORA";
        this.montoMinimo = 75000.0;
        this.categoria = "FPV";
    }

    public FondoTestDataBuilder conId(String id) {
        this.id = id;
        return this;
    }

    public FondoTestDataBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public FondoTestDataBuilder conMontoMinimo(Double montoMinimo) {
        this.montoMinimo = montoMinimo;
        return this;
    }

    public FondoTestDataBuilder conCategoria(String categoria) {
        this.categoria = categoria;
        return this;
    }

    public Fondo construir() {
        return new Fondo(id, nombre, montoMinimo, categoria);
    }
}
