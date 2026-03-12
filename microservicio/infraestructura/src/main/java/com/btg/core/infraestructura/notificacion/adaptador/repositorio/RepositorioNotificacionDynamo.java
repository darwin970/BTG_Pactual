package com.btg.core.infraestructura.notificacion.adaptador.repositorio;

import com.btg.core.dominio.notificacion.modelo.dto.NotificacionDTO;
import com.btg.core.dominio.notificacion.puerto.RepositorioNotificacion;
import com.btg.core.infraestructura.notificacion.adaptador.NotificacionItem;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class RepositorioNotificacionDynamo implements RepositorioNotificacion {

    private static final String TABLA_NOTIFICACIONES = "Notificaciones";

    private final DynamoDbTable<NotificacionItem> tabla;

    public RepositorioNotificacionDynamo(DynamoDbEnhancedClient enhancedClient) {
        this.tabla = enhancedClient.table(TABLA_NOTIFICACIONES, TableSchema.fromBean(NotificacionItem.class));
    }

    @Override
    public void guardar(NotificacionDTO notificacion) {
        NotificacionItem item = new NotificacionItem(
                notificacion.getId(),
                notificacion.getClienteId(),
                notificacion.getCanal(),
                notificacion.getDestinatario(),
                notificacion.getMensaje(),
                notificacion.getEstado(),
                notificacion.getDetalleError(),
                notificacion.getTimestamp());
        tabla.putItem(item);
    }
}
