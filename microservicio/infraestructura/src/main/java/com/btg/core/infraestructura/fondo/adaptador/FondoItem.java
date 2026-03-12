package com.btg.core.infraestructura.fondo.adaptador;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FondoItem {

    private String id;
    private String nombre;
    private Double montoMinimo;
    private String categoria;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}
