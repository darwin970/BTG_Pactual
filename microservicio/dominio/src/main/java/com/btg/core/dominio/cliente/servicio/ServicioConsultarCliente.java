package com.btg.core.dominio.cliente.servicio;

import com.btg.core.dominio.cliente.modelo.dto.ClienteDTO;
import com.btg.core.dominio.cliente.puerto.dao.DaoCliente;
import com.btg.core.dominio.excepcion.ExcepcionSinDatos;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServicioConsultarCliente {

    private static final String EL_CLIENTE_NO_EXISTE = "El cliente no existe";

    private final DaoCliente daoCliente;

    public ClienteDTO ejecutar(String id) {
        ClienteDTO cliente = daoCliente.obtenerPorId(id);
        if (cliente == null) {
            throw new ExcepcionSinDatos(EL_CLIENTE_NO_EXISTE);
        }
        return cliente;
    }
}
