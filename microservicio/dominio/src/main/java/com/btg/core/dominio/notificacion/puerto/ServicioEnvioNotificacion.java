package com.btg.core.dominio.notificacion.puerto;

public interface ServicioEnvioNotificacion {

    void enviar(String destinatario, String canal, String mensaje);
}
