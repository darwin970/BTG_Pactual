package com.btg.core.dominio.notificacion.servicio;

import com.btg.core.dominio.cliente.modelo.dto.ClienteDTO;
import com.btg.core.dominio.cliente.puerto.dao.DaoCliente;
import com.btg.core.dominio.notificacion.modelo.dto.NotificacionDTO;
import com.btg.core.dominio.notificacion.puerto.RepositorioNotificacion;
import com.btg.core.dominio.notificacion.puerto.ServicioEnvioNotificacion;
import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RequiredArgsConstructor
public class ServicioNotificacion {

    private static final String CANAL_EMAIL = "EMAIL";
    private static final String ESTADO_ENVIADO = "ENVIADO";
    private static final String ESTADO_ERROR = "ERROR";
    private static final String ESTADO_ACTIVA = "ACTIVA";

    private final DaoCliente daoCliente;
    private final ServicioEnvioNotificacion servicioEnvioNotificacion;
    private final RepositorioNotificacion repositorioNotificacion;

    public NotificacionDTO ejecutar(TransaccionDTO transaccion) {
        ClienteDTO cliente = daoCliente.obtenerPorId(transaccion.getClienteId());
        String canal = transaccion.getPreferenciaNotificacion();
        String destinatario = CANAL_EMAIL.equalsIgnoreCase(canal) ? cliente.getEmail() : cliente.getTelefono();
        String mensaje = construirMensaje(transaccion);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        try {
            servicioEnvioNotificacion.enviar(destinatario, canal, mensaje);
        } catch (Exception e) {
            NotificacionDTO trazaError = new NotificacionDTO(
                    UUID.randomUUID().toString(), transaccion.getClienteId(),
                    canal, destinatario, mensaje, ESTADO_ERROR, e.getMessage(), timestamp);
            repositorioNotificacion.guardar(trazaError);
            throw new RuntimeException("Error al enviar la notificación: " + e.getMessage(), e);
        }

        NotificacionDTO notificacion = new NotificacionDTO(
                UUID.randomUUID().toString(), transaccion.getClienteId(),
                canal, destinatario, mensaje, ESTADO_ENVIADO, null, timestamp);
        repositorioNotificacion.guardar(notificacion);
        return notificacion;
    }

    private String construirMensaje(TransaccionDTO transaccion) {
        if (ESTADO_ACTIVA.equals(transaccion.getEstado())) {
            return String.format(
                    "Su suscripción al fondo %s por COP $%.0f ha sido procesada exitosamente.",
                    transaccion.getNombreFondo(), transaccion.getMonto());
        }
        return String.format(
                "Su suscripción al fondo %s por COP $%.0f ha sido cancelada. El monto ha sido devuelto a su cuenta.",
                transaccion.getNombreFondo(), transaccion.getMonto());
    }
}
