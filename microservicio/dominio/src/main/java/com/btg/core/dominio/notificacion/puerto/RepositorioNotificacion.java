package com.btg.core.dominio.notificacion.puerto;

import com.btg.core.dominio.notificacion.modelo.dto.NotificacionDTO;

public interface RepositorioNotificacion {

    void guardar(NotificacionDTO notificacion);
}
