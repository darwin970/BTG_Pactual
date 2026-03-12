package com.btg.core.infraestructura.transaccion.adaptador.repositorio;

import com.btg.core.dominio.transaccion.modelo.entidad.Transaccion;
import com.btg.core.dominio.transaccion.puerto.repositorio.RepositorioTransaccion;
import com.btg.core.infraestructura.cliente.adaptador.ClienteItem;
import com.btg.core.infraestructura.transaccion.adaptador.TransaccionItem;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

@Repository
public class RepositorioTransaccionDynamo implements RepositorioTransaccion {

    private static final String TABLA_TRANSACCIONES = "Transacciones";
    private static final String TABLA_CLIENTES = "Clientes";

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<TransaccionItem> tablaTransacciones;
    private final DynamoDbTable<ClienteItem> tablaClientes;

    public RepositorioTransaccionDynamo(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.tablaTransacciones = enhancedClient.table(TABLA_TRANSACCIONES, TableSchema.fromBean(TransaccionItem.class));
        this.tablaClientes = enhancedClient.table(TABLA_CLIENTES, TableSchema.fromBean(ClienteItem.class));
    }

    @Override
    public String crear(Transaccion transaccion, String clienteId, Double nuevoSaldo) {
        TransaccionItem transaccionItem = new TransaccionItem(
                transaccion.getId(),
                transaccion.getFondoId(),
                transaccion.getClienteId(),
                transaccion.getMonto(),
                transaccion.getEstado(),
                transaccion.getPreferenciaNotificacion(),
                transaccion.getFecha().toString()
        );

        ClienteItem clienteExistente = tablaClientes.getItem(
                Key.builder().partitionValue(clienteId).build()
        );
        if (clienteExistente == null) {
            throw new com.btg.core.dominio.excepcion.ExcepcionSinDatos("Cliente no encontrado para actualización de saldo");
        }
        clienteExistente.setSaldo(nuevoSaldo);

        enhancedClient.transactWriteItems(TransactWriteItemsEnhancedRequest.builder()
                .addPutItem(tablaTransacciones, transaccionItem)
                .addUpdateItem(tablaClientes, clienteExistente)
                .build()
        );

        return transaccion.getId();
    }
}
