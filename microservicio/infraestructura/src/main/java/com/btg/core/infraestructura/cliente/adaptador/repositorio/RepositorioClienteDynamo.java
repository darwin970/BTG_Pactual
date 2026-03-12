package com.btg.core.infraestructura.cliente.adaptador.repositorio;

import com.btg.core.dominio.cliente.modelo.entidad.Cliente;
import com.btg.core.dominio.cliente.puerto.repositorio.RepositorioCliente;
import com.btg.core.infraestructura.cliente.adaptador.ClienteItem;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.UUID;

@Repository
public class RepositorioClienteDynamo implements RepositorioCliente {

    private static final String TABLA_CLIENTES = "Clientes";
    private static final String INDICE_EMAIL = "email-index";

    private final DynamoDbTable<ClienteItem> tabla;
    private final DynamoDbIndex<ClienteItem> indiceEmail;

    public RepositorioClienteDynamo(DynamoDbEnhancedClient enhancedClient) {
        this.tabla = enhancedClient.table(TABLA_CLIENTES, TableSchema.fromBean(ClienteItem.class));
        this.indiceEmail = tabla.index(INDICE_EMAIL);
    }

    @Override
    public String crear(Cliente cliente) {
        String id = UUID.randomUUID().toString();
        ClienteItem item = new ClienteItem(id, cliente.getNombre(), cliente.getEmail(),
                cliente.getTelefono(), cliente.getSaldo(), cliente.getContrasena(), cliente.getRol());
        tabla.putItem(item);
        return id;
    }

    @Override
    public boolean existeConEmail(String email) {
        return indiceEmail.query(QueryConditional.keyEqualTo(
                Key.builder().partitionValue(email).build()))
                .stream()
                .anyMatch(page -> !page.items().isEmpty());
    }

    @Override
    public Cliente obtenerPorEmail(String email) {
        return indiceEmail.query(QueryConditional.keyEqualTo(
                Key.builder().partitionValue(email).build()))
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .map(item -> new Cliente(item.getId(), item.getNombre(), item.getEmail(),
                        item.getTelefono(), item.getSaldo(), item.getContrasena(), item.getRol()))
                .orElse(null);
    }
}
