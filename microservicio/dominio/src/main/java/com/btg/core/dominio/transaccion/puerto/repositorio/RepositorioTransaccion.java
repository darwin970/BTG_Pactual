package com.btg.core.dominio.transaccion.puerto.repositorio;

import com.btg.core.dominio.transaccion.modelo.entidad.Transaccion;

public interface RepositorioTransaccion {

    String crear(Transaccion transaccion, String clienteId, Double nuevoSaldo);
}
