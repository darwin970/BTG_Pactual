package com.btg.core.infraestructura.notificacion.adaptador;

import com.btg.core.dominio.notificacion.puerto.ServicioEnvioNotificacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServicioEnvioNotificacionMock implements ServicioEnvioNotificacion {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicioEnvioNotificacionMock.class);

    @Override
    public void enviar(String destinatario, String canal, String mensaje) {
        LOGGER.info("[NOTIFICACION-{}] Para: {} | Mensaje: {}", canal, destinatario, mensaje);
    }
}
