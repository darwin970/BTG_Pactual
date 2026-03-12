package com.btg.core.infraestructura.cliente.controlador;

import com.btg.core.dominio.cliente.puerto.repositorio.GeneradorToken;
import com.btg.core.infraestructura.cliente.adaptador.ClienteItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ConsultaControladorClienteTest {

    private static final String ID_CLIENTE_TEST = "test-cliente-id-001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DynamoDbEnhancedClient enhancedClient;

    @Autowired
    private GeneradorToken generadorToken;

    private DynamoDbTable<ClienteItem> tablaClientes;
    private String tokenValido;

    @Before
    public void setUp() {
        tablaClientes = enhancedClient.table("Clientes", TableSchema.fromBean(ClienteItem.class));
        ClienteItem cliente = new ClienteItem(ID_CLIENTE_TEST, "Cliente Test",
                "clientetest@correo.com", "3001234567", 500000.0, null, "CLIENTE");
        tablaClientes.putItem(cliente);
        tokenValido = generadorToken.generar(ID_CLIENTE_TEST, "clientetest@correo.com", "CLIENTE").getToken();
    }

    @After
    public void limpiar() {
        tablaClientes.scan().items().forEach(tablaClientes::deleteItem);
    }

    @Test
    public void consultarClienteExistenteRetorna200ConDatos() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/clientes/{id}", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenValido))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID_CLIENTE_TEST))
                .andExpect(jsonPath("$.nombre").value("Cliente Test"))
                .andExpect(jsonPath("$.email").value("clientetest@correo.com"))
                .andExpect(jsonPath("$.telefono").value("3001234567"))
                .andExpect(jsonPath("$.saldo").value(500000.0));
    }

    @Test
    public void consultarClienteInexistenteRetorna404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/clientes/{id}", "id-inexistente-999")
                .header("Authorization", "Bearer " + tokenValido))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El cliente no existe"));
    }
}
