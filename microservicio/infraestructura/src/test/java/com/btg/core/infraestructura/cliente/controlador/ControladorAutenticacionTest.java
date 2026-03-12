package com.btg.core.infraestructura.cliente.controlador;

import com.btg.core.aplicacion.cliente.comando.ComandoAutenticar;
import com.btg.core.dominio.cliente.puerto.repositorio.EncriptadorContrasena;
import com.btg.core.dominio.cliente.puerto.repositorio.GeneradorToken;
import com.btg.core.infraestructura.cliente.adaptador.ClienteItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ControladorAutenticacionTest {

    private static final String ID_CLIENTE_TEST = "test-auth-cliente-001";
    private static final String EMAIL_TEST = "test.auth@email.com";
    private static final String CONTRASENA_TEST = "Clave123*";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DynamoDbEnhancedClient enhancedClient;

    @Autowired
    private EncriptadorContrasena encriptadorContrasena;

    @Autowired
    private GeneradorToken generadorToken;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private DynamoDbTable<ClienteItem> tablaClientes;

    @Before
    public void setUp() {
        tablaClientes = enhancedClient.table("Clientes", TableSchema.fromBean(ClienteItem.class));
        String contrasenaHash = encriptadorContrasena.encriptar(CONTRASENA_TEST);
        ClienteItem cliente = new ClienteItem(ID_CLIENTE_TEST, "Auth Test", EMAIL_TEST,
                "3001234567", 500000.0, contrasenaHash, "CLIENTE");
        tablaClientes.putItem(cliente);
    }

    @After
    public void limpiar() {
        tablaClientes.scan().items().forEach(tablaClientes::deleteItem);
    }

    @Test
    public void loginConCredencialesValidasRetorna200ConToken() throws Exception {
        Thread.sleep(1000);

        ComandoAutenticar comando = new ComandoAutenticar(EMAIL_TEST, CONTRASENA_TEST);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor.token").isNotEmpty())
                .andExpect(jsonPath("$.valor.expiracion").isNumber());
    }

    @Test
    public void loginConEmailInexistenteRetorna401() throws Exception {
        ComandoAutenticar comando = new ComandoAutenticar("noexiste@email.com", CONTRASENA_TEST);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }

    @Test
    public void loginConContrasenaIncorrectaRetorna401() throws Exception {
        Thread.sleep(1000);

        ComandoAutenticar comando = new ComandoAutenticar(EMAIL_TEST, "ContrasenaErronea123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comando)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }

    @Test
    public void accederEndpointProtegidoSinTokenRetorna401() throws Exception {
        mockMvc.perform(get("/clientes/{id}", ID_CLIENTE_TEST))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Autenticación requerida"));
    }

    @Test
    public void accederEndpointProtegidoConTokenExpiradoRetorna401() throws Exception {
        String tokenExpirado = Jwts.builder()
                .subject(ID_CLIENTE_TEST)
                .claim("email", EMAIL_TEST)
                .claim("rol", "CLIENTE")
                .issuedAt(new Date(System.currentTimeMillis() - 200000))
                .expiration(new Date(System.currentTimeMillis() - 100000))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        mockMvc.perform(get("/clientes/{id}", ID_CLIENTE_TEST)
                .header("Authorization", "Bearer " + tokenExpirado))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Token expirado"));
    }

    @Test
    public void accederEndpointAdminConRolClienteRetorna403() throws Exception {
        String token = generadorToken.generar(ID_CLIENTE_TEST, EMAIL_TEST, "CLIENTE").getToken();

        mockMvc.perform(get("/admin/test")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").value("Acceso denegado"));
    }
}
