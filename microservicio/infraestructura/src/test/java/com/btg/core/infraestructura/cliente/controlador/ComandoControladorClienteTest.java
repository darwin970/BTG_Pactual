package com.btg.core.infraestructura.cliente.controlador;

import com.btg.core.aplicacion.cliente.comando.ComandoCliente;
import com.btg.core.infraestructura.cliente.testdatabuilder.ComandoClienteTestDataBuilder;
import com.btg.core.infraestructura.cliente.adaptador.ClienteItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ComandoControladorClienteTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DynamoDbEnhancedClient enhancedClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @After
    public void limpiarClientes() {
        DynamoDbTable<ClienteItem> tabla = enhancedClient.table("Clientes", TableSchema.fromBean(ClienteItem.class));
        tabla.scan().items().forEach(tabla::deleteItem);
    }

    @Test
    public void crearClienteConDatosValidosRetorna201() throws Exception {
        ComandoCliente comando = new ComandoClienteTestDataBuilder().construir();

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor").isNotEmpty());
    }

    @Test
    public void crearClienteSinNombreRetorna400() throws Exception {
        ComandoCliente comando = new ComandoClienteTestDataBuilder().conNombre("").construir();

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El nombre del cliente es obligatorio"));
    }

    @Test
    public void crearClienteConEmailInvalidoRetorna400() throws Exception {
        ComandoCliente comando = new ComandoClienteTestDataBuilder().conEmail("email-invalido").construir();

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El email del cliente no tiene un formato válido"));
    }

    @Test
    public void crearClienteConEmailDuplicadoRetorna409() throws Exception {
        ComandoCliente primerCliente = new ComandoClienteTestDataBuilder()
                .conEmail("duplicado@correo.com").construir();

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(primerCliente)))
                .andExpect(status().isCreated());

        Thread.sleep(1000); // Esperar propagación GSI (eventually consistent)

        ComandoCliente segundoCliente = new ComandoClienteTestDataBuilder()
                .conNombre("Otro Usuario").conEmail("duplicado@correo.com")
                .conTelefono("3009876543").construir();

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(segundoCliente)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un cliente registrado con este email"));
    }
}
