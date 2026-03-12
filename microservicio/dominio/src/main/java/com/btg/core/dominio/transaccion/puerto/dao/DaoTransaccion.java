package com.btg.core.dominio.transaccion.puerto.dao;

import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;

import java.util.List;

public interface DaoTransaccion {

    TransaccionDTO obtenerPorId(String id);

    List<TransaccionDTO> obtenerPorClienteId(String clienteId);
}
