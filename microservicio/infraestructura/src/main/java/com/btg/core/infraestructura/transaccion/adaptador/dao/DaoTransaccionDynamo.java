package com.btg.core.infraestructura.transaccion.adaptador.dao;

import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import com.btg.core.dominio.transaccion.puerto.dao.DaoTransaccion;
import com.btg.core.infraestructura.transaccion.adaptador.TransaccionItem;
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
public class DaoTransaccionDynamo implements DaoTransaccion {

    private static final String TABLA_TRANSACCIONES = "Transacciones";
    private static final String INDICE_CLIENTE_ID = "clienteId-index";

    private final DynamoDbTable<TransaccionItem> tabla;
    private final DynamoDbIndex<TransaccionItem> indiceClienteId;

    public DaoTransaccionDynamo(DynamoDbEnhancedClient enhancedClient) {
        this.tabla = enhancedClient.table(TABLA_TRANSACCIONES, TableSchema.fromBean(TransaccionItem.class));
        this.indiceClienteId = tabla.index(INDICE_CLIENTE_ID);
    }

    @Override
    public TransaccionDTO obtenerPorId(String id) {
        TransaccionItem item = tabla.getItem(Key.builder().partitionValue(id).build());
        if (item == null) {
            return null;
        }
        return mapear(item);
    }

    @Override
    public List<TransaccionDTO> obtenerPorClienteId(String clienteId) {
        return indiceClienteId.query(QueryConditional.keyEqualTo(
                Key.builder().partitionValue(clienteId).build()))
                .stream()
                .flatMap(page -> page.items().stream())
                .map(this::mapear)
                .collect(Collectors.toList());
    }

    private TransaccionDTO mapear(TransaccionItem item) {
        return new TransaccionDTO(item.getId(), item.getFondoId(), null, item.getClienteId(),
                item.getMonto(), item.getEstado(), item.getPreferenciaNotificacion(),
                item.getFecha(), item.getFechaCancelacion());
    }
}
