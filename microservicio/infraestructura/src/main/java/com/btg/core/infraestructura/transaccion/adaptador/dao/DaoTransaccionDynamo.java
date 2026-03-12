package com.btg.core.infraestructura.transaccion.adaptador.dao;

import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import com.btg.core.dominio.transaccion.puerto.dao.DaoTransaccion;
import com.btg.core.infraestructura.transaccion.adaptador.TransaccionItem;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class DaoTransaccionDynamo implements DaoTransaccion {

    private static final String TABLA_TRANSACCIONES = "Transacciones";

    private final DynamoDbTable<TransaccionItem> tabla;

    public DaoTransaccionDynamo(DynamoDbEnhancedClient enhancedClient) {
        this.tabla = enhancedClient.table(TABLA_TRANSACCIONES, TableSchema.fromBean(TransaccionItem.class));
    }

    @Override
    public TransaccionDTO obtenerPorId(String id) {
        TransaccionItem item = tabla.getItem(Key.builder().partitionValue(id).build());
        if (item == null) {
            return null;
        }
        return new TransaccionDTO(item.getId(), item.getFondoId(), null, item.getClienteId(),
                item.getMonto(), item.getEstado(), item.getPreferenciaNotificacion(),
                item.getFecha(), item.getFechaCancelacion());
    }
}
