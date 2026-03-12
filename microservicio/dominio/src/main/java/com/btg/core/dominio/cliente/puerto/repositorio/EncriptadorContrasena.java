package com.btg.core.dominio.cliente.puerto.repositorio;

public interface EncriptadorContrasena {

    String encriptar(String contrasenaPlana);

    boolean verificar(String contrasenaPlana, String contrasenaEncriptada);
}
