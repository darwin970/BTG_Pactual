package com.btg.core.infraestructura.cliente.testdatabuilder;

import com.btg.core.aplicacion.cliente.comando.ComandoCliente;

public class ComandoClienteTestDataBuilder {

    private String nombre;
    private String email;
    private String telefono;
    private String contrasena;

    public ComandoClienteTestDataBuilder() {
        this.nombre = "Juan Pérez";
        this.email = "juan.perez@email.com";
        this.telefono = "3001234567";
        this.contrasena = "Clave123*";
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

    public ComandoClienteTestDataBuilder conContrasena(String contrasena) {
        this.contrasena = contrasena;
        return this;
    }

    public ComandoCliente construir() {
        return new ComandoCliente(nombre, email, telefono, contrasena);
    }
}
