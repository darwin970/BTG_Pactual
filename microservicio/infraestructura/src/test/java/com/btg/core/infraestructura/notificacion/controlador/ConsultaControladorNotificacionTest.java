package com.btg.core.infraestructura.notificacion.controlador;

import com.btg.core.aplicacion.transaccion.comando.ComandoSuscripcion;
import com.btg.core.dominio.cliente.puerto.repositorio.EncriptadorContrasena;
import com.btg.core.dominio.cliente.puerto.repositorio.GeneradorToken;
import com.btg.core.infraestructura.cliente.adaptador.ClienteItem;
import com.btg.core.infraestructura.notificacion.adaptador.NotificacionItem;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ConsultaControladorNotificacionTest {

    private static final String ID_CLIENTE_TEST = "cliente-test-notif";
    private static final String EMAIL_TEST = "notif.test@email.com";
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
    private DynamoDbTable<NotificacionItem> tablaNotificaciones;
    private String tokenCliente;

    @Before
    public void setUp() {
        tablaClientes = enhancedClient.table("Clientes", TableSchema.fromBean(ClienteItem.class));
        tablaTransacciones = enhancedClient.table("Transacciones", TableSchema.fromBean(TransaccionItem.class));
        tablaNotificaciones = enhancedClient.table("Notificaciones", TableSchema.fromBean(NotificacionItem.class));

        String contrasenaHash = encriptadorContrasena.encriptar(CONTRASENA_TEST);
        ClienteItem cliente = new ClienteItem(ID_CLIENTE_TEST, "Test Notif", EMAIL_TEST,
                "3007654321", 500000.0, contrasenaHash, "CLIENTE");
        tablaClientes.putItem(cliente);

        tokenCliente = generadorToken.generar(ID_CLIENTE_TEST, EMAIL_TEST, "CLIENTE").getToken();
    }

    @After
    public void limpiar() {
        tablaNotificaciones.scan().items().forEach(tablaNotificaciones::deleteItem);
        tablaTransacciones.scan().items().forEach(tablaTransacciones::deleteItem);
        tablaClientes.scan().items().forEach(tablaClientes::deleteItem);
    }

    @Test
    public void consultarNotificacionesVacioRetorna200ConListaVacia() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/notificaciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void suscripcionGeneraNotificacionConEstadoEnviado() throws Exception {
        ComandoSuscripcion comando = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("1").conMonto(75000.0).conPreferenciaNotificacion("EMAIL").construir();
        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isCreated());

        Thread.sleep(500);

        mockMvc.perform(get("/clientes/{clienteId}/notificaciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("ENVIADO"))
                .andExpect(jsonPath("$[0].canal").value("EMAIL"))
                .andExpect(jsonPath("$[0].destinatario").value(EMAIL_TEST));
    }

    @Test
    public void consultarNotificacionesSinTokenRetorna401() throws Exception {
        mockMvc.perform(get("/clientes/{clienteId}/notificaciones", ID_CLIENTE_TEST))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void consultarNotificacionesDeOtroClienteRetorna403() throws Exception {
        String otroClienteId = "cliente-otro-notif";
        String contrasenaHash = encriptadorContrasena.encriptar(CONTRASENA_TEST);
        ClienteItem otroCliente = new ClienteItem(otroClienteId, "Otro", "otro.notif@email.com",
                "3008888888", 500000.0, contrasenaHash, "CLIENTE");
        tablaClientes.putItem(otroCliente);
        String tokenOtro = generadorToken.generar(otroClienteId, "otro.notif@email.com", "CLIENTE").getToken();

        mockMvc.perform(get("/clientes/{clienteId}/notificaciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenOtro))
                .andExpect(status().isForbidden());
    }

    @Test
    public void suscripcionRetornaComandoRespuestaConValorYSinAdvertencia() throws Exception {
        ComandoSuscripcion comando = new ComandoSuscripcionTestDataBuilder()
                .conFondoId("1").conMonto(75000.0).conPreferenciaNotificacion("EMAIL").construir();
        mockMvc.perform(post("/clientes/{clienteId}/suscripciones", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.valor.estado").value("ACTIVA"));
    }
}
