package com.btg.core.infraestructura.configuracion;

import com.btg.core.infraestructura.cliente.adaptador.ClienteItem;
import com.btg.core.infraestructura.fondo.adaptador.FondoItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.Arrays;
import java.util.List;

@Component
public class CargaDatosSemilla implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CargaDatosSemilla.class);
    private static final String TABLA_CLIENTES = "Clientes";
    private static final String TABLA_FONDOS = "Fondos";

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbTable<ClienteItem> tablaClientes;
    private final DynamoDbTable<FondoItem> tablaFondos;

    public CargaDatosSemilla(DynamoDbClient dynamoDbClient, DynamoDbEnhancedClient enhancedClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.tablaClientes = enhancedClient.table(TABLA_CLIENTES, TableSchema.fromBean(ClienteItem.class));
        this.tablaFondos = enhancedClient.table(TABLA_FONDOS, TableSchema.fromBean(FondoItem.class));
    }

    @Override
    public void run(String... args) {
        crearTablaClientesSiNoExiste();
        crearTablaFondosSiNoExiste();

        if (tablaFondosVacia()) {
            cargarFondosSemilla();
            LOGGER.info("Datos semilla de fondos cargados exitosamente");
        } else {
            LOGGER.info("La tabla Fondos ya contiene datos, se omite la carga semilla");
        }
    }

    private void crearTablaClientesSiNoExiste() {
        if (tablaExiste(TABLA_CLIENTES)) {
            LOGGER.info("La tabla {} ya existe", TABLA_CLIENTES);
            return;
        }

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(TABLA_CLIENTES)
                .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName("email").attributeType(ScalarAttributeType.S).build()
                )
                .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                        .indexName("email-index")
                        .keySchema(KeySchemaElement.builder().attributeName("email").keyType(KeyType.HASH).build())
                        .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

        dynamoDbClient.createTable(request);
        esperarTablaActiva(TABLA_CLIENTES);
        LOGGER.info("Tabla {} creada exitosamente con GSI email-index", TABLA_CLIENTES);
    }

    private void crearTablaFondosSiNoExiste() {
        if (tablaExiste(TABLA_FONDOS)) {
            LOGGER.info("La tabla {} ya existe", TABLA_FONDOS);
            return;
        }

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(TABLA_FONDOS)
                .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

        dynamoDbClient.createTable(request);
        esperarTablaActiva(TABLA_FONDOS);
        LOGGER.info("Tabla {} creada exitosamente", TABLA_FONDOS);
    }

    private boolean tablaExiste(String nombreTabla) {
        try {
            dynamoDbClient.describeTable(r -> r.tableName(nombreTabla));
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    private void esperarTablaActiva(String nombreTabla) {
        try (DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build()) {
            waiter.waitUntilTableExists(r -> r.tableName(nombreTabla));
        }
    }

    private boolean tablaFondosVacia() {
        return tablaFondos.scan().items().stream().findFirst().isEmpty();
    }

    private void cargarFondosSemilla() {
        List<FondoItem> fondos = Arrays.asList(
                new FondoItem("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75000.0, "FPV"),
                new FondoItem("2", "FPV_BTG_PACTUAL_ECOPETROL", 125000.0, "FPV"),
                new FondoItem("3", "DEUDAPRIVADA", 50000.0, "FIC"),
                new FondoItem("4", "FDO-ACCIONES", 250000.0, "FIC"),
                new FondoItem("5", "FPV_BTG_PACTUAL_DINAMICA", 100000.0, "FPV")
        );
        fondos.forEach(tablaFondos::putItem);
    }
}
