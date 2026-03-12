package com.btg.core.infraestructura.cliente.controlador;

import com.btg.core.aplicacion.cliente.consulta.ManejadorConsultarCliente;
import com.btg.core.dominio.cliente.modelo.dto.ClienteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ConsultaControladorCliente {

    private final ManejadorConsultarCliente manejadorConsultarCliente;

    @GetMapping("/{id}")
    public ClienteDTO obtenerPorId(@PathVariable String id) {
        return manejadorConsultarCliente.ejecutar(id);
    }
}
