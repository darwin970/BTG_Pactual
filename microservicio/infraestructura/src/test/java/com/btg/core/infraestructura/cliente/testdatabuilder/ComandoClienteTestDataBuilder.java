package com.btg.core.infraestructura.cliente.testdatabuilder;

import com.btg.core.aplicacion.cliente.comando.ComandoCliente;

public class ComandoClienteTestDataBuilder {

    private String nombre;
    private String email;
    private String telefono;

    public ComandoClienteTestDataBuilder() {
        this.nombre = "Juan Pérez";
        this.email = "juan.perez@email.com";
        this.telefono = "3001234567";
    }

    public ComandoClienteTestDataBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public ComandoClienteTestDataBuilder conEmail(String email) {
        this.email = email;
        return this;
    }

    public ComandoClienteTestDataBuilder conTelefono(String telefono) {
        this.telefono = telefono;
        return this;
    }

    public ComandoCliente construir() {
        return new ComandoCliente(nombre, email, telefono);
    }
}
