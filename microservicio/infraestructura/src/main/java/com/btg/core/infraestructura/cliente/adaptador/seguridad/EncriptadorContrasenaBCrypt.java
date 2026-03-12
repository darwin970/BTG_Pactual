package com.btg.core.infraestructura.cliente.adaptador.seguridad;

import com.btg.core.dominio.cliente.puerto.repositorio.EncriptadorContrasena;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncriptadorContrasenaBCrypt implements EncriptadorContrasena {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encriptar(String contrasenaPlana) {
        return encoder.encode(contrasenaPlana);
    }

    @Override
    public boolean verificar(String contrasenaPlana, String contrasenaEncriptada) {
        return encoder.matches(contrasenaPlana, contrasenaEncriptada);
    }
}
