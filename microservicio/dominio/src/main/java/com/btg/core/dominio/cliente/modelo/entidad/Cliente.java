package com.btg.core.dominio.cliente.modelo.entidad;

import com.btg.core.dominio.ValidadorArgumento;
import lombok.Getter;

@Getter
public class Cliente {

    private static final String REGEX_EMAIL = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static final double SALDO_INICIAL = 500000.0;
    public static final String ROL_CLIENTE = "CLIENTE";

    private final String id;
    private final String nombre;
    private final String email;
    private final String telefono;
    private final Double saldo;
    private final String contrasena;
    private final String rol;

    public Cliente(String id, String nombre, String email, String telefono, Double saldo,
                   String contrasena, String rol) {
        ValidadorArgumento.validarObligatorio(nombre, "El nombre del cliente es obligatorio");
        ValidadorArgumento.validarObligatorio(email, "El email del cliente es obligatorio");
        ValidadorArgumento.validarRegex(email, REGEX_EMAIL, "El email del cliente no tiene un formato válido");
        ValidadorArgumento.validarObligatorio(telefono, "El teléfono del cliente es obligatorio");
        ValidadorArgumento.validarNumerico(telefono, "El teléfono del cliente debe ser numérico");

        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.saldo = saldo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public Cliente(String nombre, String email, String telefono, String contrasena) {
        this(null, nombre, email, telefono, SALDO_INICIAL, contrasena, ROL_CLIENTE);
        ValidadorArgumento.validarObligatorio(contrasena, "La contraseña del cliente es obligatoria");
    }

    public static double getSaldoInicial() {
        return SALDO_INICIAL;
    }
}
