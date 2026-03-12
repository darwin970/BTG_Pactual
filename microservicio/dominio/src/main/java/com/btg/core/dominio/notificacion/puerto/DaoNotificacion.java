package com.btg.core.dominio.notificacion.puerto;

import com.btg.core.dominio.notificacion.modelo.dto.NotificacionDTO;

import java.util.List;

public interface DaoNotificacion {

    List<NotificacionDTO> obtenerPorClienteId(String clienteId);
}
