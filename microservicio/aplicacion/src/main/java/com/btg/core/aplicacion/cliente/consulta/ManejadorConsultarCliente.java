package com.btg.core.aplicacion.cliente.consulta;

import com.btg.core.dominio.cliente.modelo.dto.ClienteDTO;
import com.btg.core.dominio.cliente.servicio.ServicioConsultarCliente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManejadorConsultarCliente {

    private final ServicioConsultarCliente servicioConsultarCliente;

    public ClienteDTO ejecutar(String id) {
        return servicioConsultarCliente.ejecutar(id);
    }
}
