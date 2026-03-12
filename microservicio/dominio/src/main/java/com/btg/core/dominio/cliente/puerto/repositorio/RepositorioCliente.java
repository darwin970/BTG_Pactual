package com.btg.core.dominio.cliente.puerto.repositorio;

import com.btg.core.dominio.cliente.modelo.entidad.Cliente;

public interface RepositorioCliente {

    String crear(Cliente cliente);

    boolean existeConEmail(String email);

    Cliente obtenerPorEmail(String email);
}
