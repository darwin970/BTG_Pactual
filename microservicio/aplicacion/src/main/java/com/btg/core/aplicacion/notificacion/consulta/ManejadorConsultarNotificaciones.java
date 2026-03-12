package com.btg.core.aplicacion.notificacion.consulta;

import com.btg.core.dominio.notificacion.modelo.dto.NotificacionDTO;
import com.btg.core.dominio.notificacion.puerto.DaoNotificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ManejadorConsultarNotificaciones {

    private final DaoNotificacion daoNotificacion;

    public List<NotificacionDTO> ejecutar(String clienteId) {
        return daoNotificacion.obtenerPorClienteId(clienteId);
    }
}
