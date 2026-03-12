package com.btg.core.aplicacion.transaccion.consulta;

import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import com.btg.core.dominio.transaccion.servicio.ServicioConsultarTransacciones;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ManejadorConsultarTransacciones {

    private final ServicioConsultarTransacciones servicioConsultarTransacciones;

    public List<TransaccionDTO> ejecutar(String clienteId) {
        return servicioConsultarTransacciones.ejecutar(clienteId);
    }
}
