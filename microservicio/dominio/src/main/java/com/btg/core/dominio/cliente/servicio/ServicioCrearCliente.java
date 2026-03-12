package com.btg.core.dominio.cliente.servicio;

import com.btg.core.dominio.cliente.modelo.entidad.Cliente;
import com.btg.core.dominio.cliente.puerto.repositorio.EncriptadorContrasena;
import com.btg.core.dominio.cliente.puerto.repositorio.RepositorioCliente;
import com.btg.core.dominio.excepcion.ExcepcionDuplicidad;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServicioCrearCliente {

    private static final String YA_EXISTE_CLIENTE_CON_EMAIL = "Ya existe un cliente registrado con este email";

    private final RepositorioCliente repositorioCliente;
    private final EncriptadorContrasena encriptadorContrasena;

    public String ejecutar(Cliente cliente) {
        if (repositorioCliente.existeConEmail(cliente.getEmail())) {
            throw new ExcepcionDuplicidad(YA_EXISTE_CLIENTE_CON_EMAIL);
        }
        String contrasenaEncriptada = encriptadorContrasena.encriptar(cliente.getContrasena());
        Cliente clienteConContrasenaEncriptada = new Cliente(
                cliente.getId(), cliente.getNombre(), cliente.getEmail(),
                cliente.getTelefono(), cliente.getSaldo(),
                contrasenaEncriptada, cliente.getRol());
        return repositorioCliente.crear(clienteConContrasenaEncriptada);
    }
}
