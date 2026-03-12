package com.btg.core.dominio.cliente.modelo.entidad;

public class ClienteTestDataBuilder {

    private String id;
    private String nombre;
    private String email;
    private String telefono;
    private Double saldo;
    private String contrasena;
    private String rol;

    public ClienteTestDataBuilder() {
        this.id = "abc-123-def";
        this.nombre = "Juan Pérez";
        this.email = "juan.perez@email.com";
        this.telefono = "3001234567";
        this.saldo = 500000.0;
        this.contrasena = "Clave123*";
        this.rol = "CLIENTE";
    }

    public ClienteTestDataBuilder conId(String id) {
        this.id = id;
        return this;
    }

    public ClienteTestDataBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public ClienteTestDataBuilder conEmail(String email) {
        this.email = email;
        return this;
    }

    public ClienteTestDataBuilder conTelefono(String telefono) {
        this.telefono = telefono;
        return this;
    }

    public ClienteTestDataBuilder conSaldo(Double saldo) {
        this.saldo = saldo;
        return this;
    }

    public ClienteTestDataBuilder conContrasena(String contrasena) {
        this.contrasena = contrasena;
        return this;
    }

    public ClienteTestDataBuilder conRol(String rol) {
        this.rol = rol;
        return this;
    }

    public Cliente construir() {
        return new Cliente(id, nombre, email, telefono, saldo, contrasena, rol);
    }
}
