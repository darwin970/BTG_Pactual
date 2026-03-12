package com.btg.core.infraestructura.cliente.adaptador.dao;

import com.btg.core.dominio.cliente.modelo.dto.ClienteDTO;
import com.btg.core.dominio.cliente.puerto.dao.DaoCliente;
import com.btg.core.infraestructura.cliente.adaptador.ClienteItem;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class DaoClienteDynamo implements DaoCliente {

    private static final String TABLA_CLIENTES = "Clientes";

    private final DynamoDbTable<ClienteItem> tabla;

    public DaoClienteDynamo(DynamoDbEnhancedClient enhancedClient) {
        this.tabla = enhancedClient.table(TABLA_CLIENTES, TableSchema.fromBean(ClienteItem.class));
    }

    @Override
    public ClienteDTO obtenerPorId(String id) {
        ClienteItem item = tabla.getItem(Key.builder().partitionValue(id).build());
        if (item == null) {
            return null;
        }
        return new ClienteDTO(item.getId(), item.getNombre(), item.getEmail(),
                item.getTelefono(), item.getSaldo(), item.getRol());
    }
}
