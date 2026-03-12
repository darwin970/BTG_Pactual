package com.btg.core.dominio.cliente.servicio;

import com.btg.core.dominio.cliente.modelo.entidad.Cliente;
import com.btg.core.dominio.cliente.puerto.repositorio.RepositorioCliente;
import com.btg.core.dominio.excepcion.ExcepcionDuplicidad;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServicioCrearCliente {

    private static final String YA_EXISTE_CLIENTE_CON_EMAIL = "Ya existe un cliente registrado con este email";

    private final RepositorioCliente repositorioCliente;

    public String ejecutar(Cliente cliente) {
        if (repositorioCliente.existeConEmail(cliente.getEmail())) {
            throw new ExcepcionDuplicidad(YA_EXISTE_CLIENTE_CON_EMAIL);
        }
        return repositorioCliente.crear(cliente);
    }
}
