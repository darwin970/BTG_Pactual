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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ComandoControladorTransaccionTest {

    private static final String ID_CLIENTE_TEST = "cliente-test-suscripcion";
    private static final String EMAIL_TEST = "suscripcion.test@email.com";
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
        ClienteItem cliente = new ClienteItem(ID_CLIENTE_TEST, "Test Suscripcion", EMAIL_TEST,
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
    public void suscribirFondoExitosamenteRetorna201() throws Exception {
        ComandoSuscripcion comando = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("1").conMonto(75000.0).conPreferenciaNotificacion("EMAIL").construir();

        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor.id").isNotEmpty())
                .andExpect(jsonPath("$.valor.fondoId").value("1"))
                .andExpect(jsonPath("$.valor.nombreFondo").value("FPV_BTG_PACTUAL_RECAUDADORA"))
                .andExpect(jsonPath("$.valor.clienteId").value(ID_CLIENTE_TEST))
                .andExpect(jsonPath("$.valor.monto").value(75000.0))
                .andExpect(jsonPath("$.valor.estado").value("ACTIVA"))
                .andExpect(jsonPath("$.valor.preferenciaNotificacion").value("EMAIL"))
                .andExpect(jsonPath("$.valor.fecha").isNotEmpty());
    }

    @Test
    public void suscribirFondoConSaldoInsuficienteRetorna400() throws Exception {
        ComandoSuscripcion comando = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("1").conMonto(600000.0).conPreferenciaNotificacion("EMAIL").construir();

        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No tiene saldo disponible para vincularse al fondo FPV_BTG_PACTUAL_RECAUDADORA"));
    }

    @Test
    public void suscribirFondoInexistenteRetorna404() throws Exception {
        ComandoSuscripcion comando = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("999").conMonto(75000.0).conPreferenciaNotificacion("EMAIL").construir();

        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Fondo no encontrado"));
    }

    @Test
    public void suscribirConClienteInexistenteRetorna404() throws Exception {
        String otroClienteId = "cliente-no-existe";
        String tokenOtro = generadorToken.generar(otroClienteId, "otro@email.com", "CLIENTE").getToken();

        ComandoSuscripcion comando = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("1").conMonto(75000.0).conPreferenciaNotificacion("EMAIL").construir();

        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", otroClienteId)
                .header("Authorization", "Bearer " + tokenOtro)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El cliente no existe"));
    }

    @Test
    public void suscribirSinTokenRetorna401() throws Exception {
        ComandoSuscripcion comando = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("1").conMonto(75000.0).construir();

        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void suscribirConTokenDeOtroClienteRetorna403() throws Exception {
        String otroClienteId = "cliente-otro-test";
        String contrasenaHash = encriptadorContrasena.encriptar(CONTRASENA_TEST);
        ClienteItem otroCliente = new ClienteItem(otroClienteId, "Otro Cliente", "otro@email.com",
                "3009999999", 500000.0, contrasenaHash, "CLIENTE");
        tablaClientes.putItem(otroCliente);

        String tokenOtro = generadorToken.generar(otroClienteId, "otro@email.com", "CLIENTE").getToken();

        ComandoSuscripcion comando = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("1").conMonto(75000.0).construir();

        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenOtro)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void suscribirConPreferenciaSmsExitosamente() throws Exception {
        ComandoSuscripcion comando = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("3").conMonto(50000.0).conPreferenciaNotificacion("SMS").construir();

        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor.preferenciaNotificacion").value("SMS"));
    }

    @Test
    public void suscripcionMultipleFondosDescuentoAcumulativo() throws Exception {
        // Primera suscripción: 75000 → saldo queda en 425000
        ComandoSuscripcion comando1 = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("1").conMonto(75000.0).conPreferenciaNotificacion("EMAIL").construir();

        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando1)))
                .andExpect(status().isCreated());

        // Segunda suscripción: 50000 → saldo queda en 375000
        ComandoSuscripcion comando2 = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("3").conMonto(50000.0).conPreferenciaNotificacion("SMS").construir();

        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando2)))
                .andExpect(status().isCreated());

        // Tercera suscripción con monto que excede saldo restante → debe fallar
        ComandoSuscripcion comando3 = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("4").conMonto(400000.0).conPreferenciaNotificacion("EMAIL").construir();

        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando3)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No tiene saldo disponible para vincularse al fondo FDO-ACCIONES"));
    }

    @Test
    public void suscribirVerificaPersistenciaSaldo() throws Exception {
        // Suscribir por 75000 (saldo inicial 500000 → queda 425000)
        ComandoSuscripcion comando = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("1").conMonto(75000.0).conPreferenciaNotificacion("EMAIL").construir();

        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isCreated());

        // Verificar que el saldo se actualizó en DynamoDB
        ClienteItem clienteActualizado = tablaClientes.getItem(
                software.amazon.awssdk.enhanced.dynamodb.Key.builder().partitionValue(ID_CLIENTE_TEST).build()
        );
        org.junit.Assert.assertEquals(425000.0, clienteActualizado.getSaldo(), 0.01);
    }
}
