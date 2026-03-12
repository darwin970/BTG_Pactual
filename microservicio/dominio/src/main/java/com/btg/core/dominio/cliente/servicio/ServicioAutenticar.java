package com.btg.core.dominio.cliente.servicio;

import com.btg.core.dominio.cliente.modelo.entidad.Cliente;
import com.btg.core.dominio.cliente.modelo.entidad.RespuestaAutenticacion;
import com.btg.core.dominio.cliente.puerto.repositorio.EncriptadorContrasena;
import com.btg.core.dominio.cliente.puerto.repositorio.GeneradorToken;
import com.btg.core.dominio.cliente.puerto.repositorio.RepositorioCliente;
import com.btg.core.dominio.excepcion.ExcepcionAutenticacion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServicioAutenticar {

    private static final String CREDENCIALES_INVALIDAS = "Credenciales inválidas";

    private final RepositorioCliente repositorioCliente;
    private final EncriptadorContrasena encriptadorContrasena;
    private final GeneradorToken generadorToken;

    public RespuestaAutenticacion ejecutar(String email, String contrasena) {
        Cliente cliente = repositorioCliente.obtenerPorEmail(email);
        if (cliente == null) {
            throw new ExcepcionAutenticacion(CREDENCIALES_INVALIDAS);
        }
        if (!encriptadorContrasena.verificar(contrasena, cliente.getContrasena())) {
            throw new ExcepcionAutenticacion(CREDENCIALES_INVALIDAS);
        }
        return generadorToken.generar(cliente.getId(), cliente.getEmail(), cliente.getRol());
    }
}
