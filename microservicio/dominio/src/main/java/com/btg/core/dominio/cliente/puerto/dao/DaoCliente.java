package com.btg.core.dominio.cliente.puerto.dao;

import com.btg.core.dominio.cliente.modelo.dto.ClienteDTO;

public interface DaoCliente {

    ClienteDTO obtenerPorId(String id);
}
