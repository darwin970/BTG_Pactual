package com.btg.core.infraestructura.fondo.controlador;

import com.btg.core.aplicacion.fondo.consulta.ManejadorListarFondos;
import com.btg.core.dominio.fondo.modelo.dto.FondoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fondos")
@RequiredArgsConstructor
public class ConsultaControladorFondo {

    private final ManejadorListarFondos manejadorListarFondos;

    @GetMapping
    public List<FondoDTO> listarTodos() {
        return manejadorListarFondos.ejecutar();
    }
}
