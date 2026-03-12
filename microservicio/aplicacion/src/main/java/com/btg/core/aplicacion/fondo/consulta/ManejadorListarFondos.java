package com.btg.core.aplicacion.fondo.consulta;

import com.btg.core.dominio.fondo.modelo.dto.FondoDTO;
import com.btg.core.dominio.fondo.puerto.dao.DaoFondo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ManejadorListarFondos {

    private final DaoFondo daoFondo;

    public List<FondoDTO> ejecutar() {
        return daoFondo.listarTodos();
    }
}
