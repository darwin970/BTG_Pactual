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
import org.springframework.test.web.servlet.MvcResult;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ComandoControladorCancelacionTest {

    private static final String ID_CLIENTE_TEST = "cliente-test-cancelacion";
    private static final String EMAIL_TEST = "cancelacion.test@email.com";
    private static final String CONTRASENA_TEST = "Clave123*";
    private static final double SALDO_INICIAL = 500000.0;
    private static final double MONTO_SUSCRIPCION = 75000.0;

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
    private String transaccionId;

    @Before
    public void setUp() throws Exception {
        tablaClientes = enhancedClient.table("Clientes", TableSchema.fromBean(ClienteItem.class));
        tablaTransacciones = enhancedClient.table("Transacciones", TableSchema.fromBean(TransaccionItem.class));

        String contrasenaHash = encriptadorContrasena.encriptar(CONTRASENA_TEST);
        ClienteItem cliente = new ClienteItem(ID_CLIENTE_TEST, "Test Cancelacion", EMAIL_TEST,
                "3001234567", SALDO_INICIAL, contrasenaHash, "CLIENTE");
        tablaClientes.putItem(cliente);

        tokenCliente = generadorToken.generar(ID_CLIENTE_TEST, EMAIL_TEST, "CLIENTE").getToken();

        ComandoSuscripcion comandoSuscripcion = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("1").conMonto(MONTO_SUSCRIPCION).conPreferenciaNotificacion("EMAIL").construir();

        MvcResult resultado = mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comandoSuscripcion)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = resultado.getResponse().getContentAsString();
        transaccionId = objectMapper.readTree(responseBody).get("valor").get("id").asText();
    }

    @After
    public void limpiar() {
        tablaTransacciones.scan().items().forEach(tablaTransacciones::deleteItem);
        tablaClientes.scan().items().forEach(tablaClientes::deleteItem);
    }

    @Test
    public void cancelarSuscripcionExitosamenteRetorna200() throws Exception {
        mockMvc.perform(delete("/clientes/{clienteId}/suscripciones/{transaccionId}", ID_CLIENTE_TEST, transaccionId)
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor.id").value(transaccionId))
                .andExpect(jsonPath("$.valor.estado").value("CANCELADA"))
                .andExpect(jsonPath("$.valor.fechaCancelacion").isNotEmpty())
                .andExpect(jsonPath("$.valor.fondoId").value("1"))
                .andExpect(jsonPath("$.valor.nombreFondo").value("FPV_BTG_PACTUAL_RECAUDADORA"))
                .andExpect(jsonPath("$.valor.monto").value(MONTO_SUSCRIPCION));
    }

    @Test
    public void cancelarSuscripcionRestauraSaldo() throws Exception {
        mockMvc.perform(delete("/clientes/{clienteId}/suscripciones/{transaccionId}", ID_CLIENTE_TEST, transaccionId)
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk());

        ClienteItem clienteActualizado = tablaClientes.getItem(
                Key.builder().partitionValue(ID_CLIENTE_TEST).build());
        org.junit.Assert.assertEquals(SALDO_INICIAL, clienteActualizado.getSaldo(), 0.01);
    }

    @Test
    public void cancelarTransaccionInexistenteRetorna404() throws Exception {
        mockMvc.perform(delete("/clientes/{clienteId}/suscripciones/{transaccionId}", ID_CLIENTE_TEST, "txn-no-existe")
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("La transacción no existe"));
    }

    @Test
    public void cancelarTransaccionYaCanceladaRetorna400() throws Exception {
        mockMvc.perform(delete("/clientes/{clienteId}/suscripciones/{transaccionId}", ID_CLIENTE_TEST, transaccionId)
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/clientes/{clienteId}/suscripciones/{transaccionId}", ID_CLIENTE_TEST, transaccionId)
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No tiene suscripción activa al fondo FPV_BTG_PACTUAL_RECAUDADORA"));
    }

    @Test
    public void cancelarSinTokenRetorna401() throws Exception {
        mockMvc.perform(delete("/clientes/{clienteId}/suscripciones/{transaccionId}", ID_CLIENTE_TEST, transaccionId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void cancelarConTokenDeOtroClienteRetorna403() throws Exception {
        String otroClienteId = "cliente-otro-cancel";
        String contrasenaHash = encriptadorContrasena.encriptar(CONTRASENA_TEST);
        ClienteItem otroCliente = new ClienteItem(otroClienteId, "Otro Cliente", "otro.cancel@email.com",
                "3009999999", 500000.0, contrasenaHash, "CLIENTE");
        tablaClientes.putItem(otroCliente);

        String tokenOtro = generadorToken.generar(otroClienteId, "otro.cancel@email.com", "CLIENTE").getToken();

        mockMvc.perform(delete("/clientes/{clienteId}/suscripciones/{transaccionId}", ID_CLIENTE_TEST, transaccionId)
                .header("Authorization", "Bearer " + tokenOtro))
                .andExpect(status().isForbidden());
    }
}
