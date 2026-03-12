package com.btg.core.dominio.cliente.servicio;

import com.btg.core.BasePrueba;
import com.btg.core.dominio.cliente.modelo.entidad.Cliente;
import com.btg.core.dominio.cliente.modelo.entidad.ClienteTestDataBuilder;
import com.btg.core.dominio.cliente.modelo.entidad.RespuestaAutenticacion;
import com.btg.core.dominio.cliente.puerto.repositorio.EncriptadorContrasena;
import com.btg.core.dominio.cliente.puerto.repositorio.GeneradorToken;
import com.btg.core.dominio.cliente.puerto.repositorio.RepositorioCliente;
import com.btg.core.dominio.excepcion.ExcepcionAutenticacion;
import com.btg.core.dominio.excepcion.ExcepcionDuplicidad;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServicioAutenticarTest {

    private RepositorioCliente repositorioCliente;
    private EncriptadorContrasena encriptadorContrasena;
    private GeneradorToken generadorToken;
    private ServicioAutenticar servicioAutenticar;

    @Before
    public void setUp() {
        repositorioCliente = mock(RepositorioCliente.class);
        encriptadorContrasena = mock(EncriptadorContrasena.class);
        generadorToken = mock(GeneradorToken.class);
        servicioAutenticar = new ServicioAutenticar(repositorioCliente, encriptadorContrasena, generadorToken);
    }

    @Test
    public void autenticarConCredencialesValidasRetornaToken() {
        // Arrange
        Cliente cliente = new ClienteTestDataBuilder()
                .conContrasena("$2a$10$hashedPassword").construir();
        when(repositorioCliente.obtenerPorEmail("juan.perez@email.com")).thenReturn(cliente);
        when(encriptadorContrasena.verificar("Clave123*", "$2a$10$hashedPassword")).thenReturn(true);
        when(generadorToken.generar("abc-123-def", "juan.perez@email.com", "CLIENTE"))
                .thenReturn(new RespuestaAutenticacion("jwt-token-generado", 1741987200000L));

        // Act
        RespuestaAutenticacion respuesta = servicioAutenticar.ejecutar("juan.perez@email.com", "Clave123*");

        // Assert
        assertNotNull(respuesta);
        assertEquals("jwt-token-generado", respuesta.getToken());
    }

    @Test
    public void autenticarConEmailInexistenteLanzaExcepcion() {
        // Arrange
        when(repositorioCliente.obtenerPorEmail("noexiste@email.com")).thenReturn(null);

        // Act - Assert
        BasePrueba.assertThrows(
                () -> servicioAutenticar.ejecutar("noexiste@email.com", "Clave123*"),
                ExcepcionAutenticacion.class,
                "Credenciales inválidas"
        );
    }

    @Test
    public void autenticarConContrasenaIncorrectaLanzaExcepcion() {
        // Arrange
        Cliente cliente = new ClienteTestDataBuilder()
                .conContrasena("$2a$10$hashedPassword").construir();
        when(repositorioCliente.obtenerPorEmail("juan.perez@email.com")).thenReturn(cliente);
        when(encriptadorContrasena.verificar("claveIncorrecta", "$2a$10$hashedPassword")).thenReturn(false);

        // Act - Assert
        BasePrueba.assertThrows(
                () -> servicioAutenticar.ejecutar("juan.perez@email.com", "claveIncorrecta"),
                ExcepcionAutenticacion.class,
                "Credenciales inválidas"
        );
    }

    @Test
    public void servicioCrearClienteEncriptaContrasenaAntesDePersistir() {
        // Arrange
        RepositorioCliente repo = mock(RepositorioCliente.class);
        EncriptadorContrasena encriptador = mock(EncriptadorContrasena.class);
        ServicioCrearCliente servicioCrear = new ServicioCrearCliente(repo, encriptador);

        Cliente cliente = new ClienteTestDataBuilder().conId(null).construir();
        when(repo.existeConEmail(anyString())).thenReturn(false);
        when(encriptador.encriptar("Clave123*")).thenReturn("$2a$10$encrypted");
        when(repo.crear(any(Cliente.class))).thenReturn("nuevo-id");

        // Act
        servicioCrear.ejecutar(cliente);

        // Assert
        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(repo).crear(captor.capture());
        assertEquals("$2a$10$encrypted", captor.getValue().getContrasena());
    }
}
