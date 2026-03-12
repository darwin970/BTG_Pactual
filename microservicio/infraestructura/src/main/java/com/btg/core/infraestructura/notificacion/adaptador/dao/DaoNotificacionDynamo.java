package com.btg.core.infraestructura.notificacion.adaptador.dao;

import com.btg.core.dominio.notificacion.modelo.dto.NotificacionDTO;
import com.btg.core.dominio.notificacion.puerto.DaoNotificacion;
import com.btg.core.infraestructura.notificacion.adaptador.NotificacionItem;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DaoNotificacionDynamo implements DaoNotificacion {

    private static final String TABLA_NOTIFICACIONES = "Notificaciones";
    private static final String INDICE_CLIENTE_ID = "clienteId-index";

    private final DynamoDbIndex<NotificacionItem> indiceClienteId;

    public DaoNotificacionDynamo(DynamoDbEnhancedClient enhancedClient) {
        DynamoDbTable<NotificacionItem> tabla = enhancedClient.table(TABLA_NOTIFICACIONES,
                TableSchema.fromBean(NotificacionItem.class));
        this.indiceClienteId = tabla.index(INDICE_CLIENTE_ID);
    }

    @Override
    public List<NotificacionDTO> obtenerPorClienteId(String clienteId) {
        return indiceClienteId.query(QueryConditional.keyEqualTo(
                Key.builder().partitionValue(clienteId).build()))
                .stream()
                .flatMap(page -> page.items().stream())
                .map(this::mapear)
                .collect(Collectors.toList());
    }

    private NotificacionDTO mapear(NotificacionItem item) {
        return new NotificacionDTO(item.getId(), item.getClienteId(), item.getCanal(),
                item.getDestinatario(), item.getMensaje(), item.getEstado(),
                item.getDetalleError(), item.getTimestamp());
    }
}
