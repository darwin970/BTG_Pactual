package com.btg.core.infraestructura.fondo.adaptador.dao;

import com.btg.core.dominio.fondo.modelo.dto.FondoDTO;
import com.btg.core.dominio.fondo.puerto.dao.DaoFondo;
import com.btg.core.infraestructura.fondo.adaptador.FondoItem;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DaoFondoDynamo implements DaoFondo {

    private static final String TABLA_FONDOS = "Fondos";

    private final DynamoDbTable<FondoItem> tabla;

    public DaoFondoDynamo(DynamoDbEnhancedClient enhancedClient) {
        this.tabla = enhancedClient.table(TABLA_FONDOS, TableSchema.fromBean(FondoItem.class));
    }

    @Override
    public List<FondoDTO> listarTodos() {
        return tabla.scan().items().stream()
                .map(item -> new FondoDTO(item.getId(), item.getNombre(),
                        item.getMontoMinimo(), item.getCategoria()))
                .collect(Collectors.toList());
    }
}
