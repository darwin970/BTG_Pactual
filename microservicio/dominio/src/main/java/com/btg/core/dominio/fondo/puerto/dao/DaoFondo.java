package com.btg.core.dominio.fondo.puerto.dao;

import com.btg.core.dominio.fondo.modelo.dto.FondoDTO;

import java.util.List;

public interface DaoFondo {

    List<FondoDTO> listarTodos();

    FondoDTO obtenerPorId(String id);
}
