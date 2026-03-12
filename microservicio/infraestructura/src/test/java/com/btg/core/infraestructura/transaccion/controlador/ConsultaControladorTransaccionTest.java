package com.btg.core.infraestructura.transaccion.controlador;

import com.btg.core.aplicacion.transaccion.comando.ComandoSuscripcion;
import com.btg.core.dominio.cliente.puerto.repositorio.EncriptadorContrasena;
import com.btg.core.dominio.cliente.puerto.repositorio.GeneradorToken;
import com.btg.core.infraestructura.cliente.adaptador.ClienteItem;
import com.btg.core.infraestructura.transaccion.adaptador.TransaccionItem;
import com.btg.core.infraestructura.transaccion.testdatabuilder.ComandoSuscripcionTestDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ConsultaControladorTransaccionTest {

    private static final String ID_CLIENTE_TEST = "cliente-test-historial";
    private static final String EMAIL_TEST = "historial.test@email.com";
    private static final String CONTRASENA_TEST = "Clave123*";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DynamoDbEnhancedClient enhancedClient;

    @Autowired
    private EncriptadorContrasena encriptadorContrasena;

    @Autowired
    private GeneradorToken generadorToken;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private DynamoDbTable<ClienteItem> tablaClientes;
    private DynamoDbTable<TransaccionItem> tablaTransacciones;
    private String tokenCliente;

    @Before
    public void setUp() {
        tablaClientes = enhancedClient.table("Clientes", TableSchema.fromBean(ClienteItem.class));
        tablaTransacciones = enhancedClient.table("Transacciones", TableSchema.fromBean(TransaccionItem.class));

        String contrasenaHash = encriptadorContrasena.encriptar(CONTRASENA_TEST);
        ClienteItem cliente = new ClienteItem(ID_CLIENTE_TEST, "Test Historial", EMAIL_TEST,
                "3001234567", 500000.0, contrasenaHash, "CLIENTE");
        tablaClientes.putItem(cliente);

        tokenCliente = generadorToken.generar(ID_CLIENTE_TEST, EMAIL_TEST, "CLIENTE").getToken();
    }

    @After
    public void limpiar() {
        tablaTransacciones.scan().items().forEach(tablaTransacciones::deleteItem);
        tablaClientes.scan().items().forEach(tablaClientes::deleteItem);
    }

    @Test
    public void consultarHistorialVacioRetorna200ConListaVacia() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/transacciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void consultarHistorialConTransaccionesRetorna200() throws Exception {
        ComandoSuscripcion comando = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("1").conMonto(75000.0).conPreferenciaNotificacion("EMAIL").construir();
        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isCreated());

        Thread.sleep(500);

        mockMvc.perform(get("/clientes/{clienteId}/transacciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("ACTIVA"))
                .andExpect(jsonPath("$[0].monto").value(75000.0))
                .andExpect(jsonPath("$[0].fechaCancelacion").value("N/A"));
    }

    @Test
    public void consultarHistorialMuestraTransaccionesCanceladas() throws Exception {
        ComandoSuscripcion comando = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("1").conMonto(75000.0).conPreferenciaNotificacion("EMAIL").construir();
        String response = mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String transaccionId = objectMapper.readTree(response).get("valor").get("id").asText();

        mockMvc.perform(delete("/clientes/{clienteId}/suscripciones/{transaccionId}", ID_CLIENTE_TEST, transaccionId)
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk());

        Thread.sleep(500);

        mockMvc.perform(get("/clientes/{clienteId}/transacciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("CANCELADA"))
                .andExpect(jsonPath("$[0].fechaCancelacion").isNotEmpty());
    }

    @Test
    public void consultarHistorialClienteInexistenteRetorna404() throws Exception {
        String otroClienteId = "cliente-inexistente-historial";
        String tokenOtro = generadorToken.generar(otroClienteId, "otro@email.com", "CLIENTE").getToken();

        mockMvc.perform(get("/clientes/{clienteId}/transacciones", otroClienteId)
                .header("Authorization", "Bearer " + tokenOtro))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El cliente no existe"));
    }

    @Test
    public void consultarHistorialSinTokenRetorna401() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/transacciones", ID_CLIENTE_TEST))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void consultarHistorialDeOtroClienteRetorna403() throws Exception {
        String otroClienteId = "cliente-otro-historial";
        String contrasenaHash = encriptadorContrasena.encriptar(CONTRASENA_TEST);
        ClienteItem otroCliente = new ClienteItem(otroClienteId, "Otro", "otro.historial@email.com",
                "3009999999", 500000.0, contrasenaHash, "CLIENTE");
        tablaClientes.putItem(otroCliente);
        String tokenOtro = generadorToken.generar(otroClienteId, "otro.historial@email.com", "CLIENTE").getToken();

        mockMvc.perform(get("/clientes/{clienteId}/transacciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenOtro))
                .andExpect(status().isForbidden());
    }
}
