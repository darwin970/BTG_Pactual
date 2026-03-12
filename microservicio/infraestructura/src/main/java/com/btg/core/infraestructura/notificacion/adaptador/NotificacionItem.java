package com.btg.core.infraestructura.notificacion.adaptador;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionItem {

    private String id;
    private String clienteId;
    private String canal;
    private String destinatario;
    private String mensaje;
    private String estado;
    private String detalleError;
    private String timestamp;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "clienteId-index")
    public String getClienteId() {
        return clienteId;
    }
}
