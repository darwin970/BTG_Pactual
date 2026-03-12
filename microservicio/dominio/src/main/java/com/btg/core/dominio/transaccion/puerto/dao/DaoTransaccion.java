package com.btg.core.dominio.transaccion.puerto.dao;

import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;

public interface DaoTransaccion {

    TransaccionDTO obtenerPorId(String id);
}
