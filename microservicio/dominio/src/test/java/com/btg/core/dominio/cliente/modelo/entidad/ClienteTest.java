package com.btg.core.dominio.cliente.modelo.entidad;

import com.btg.core.BasePrueba;
import com.btg.core.dominio.excepcion.ExcepcionValorInvalido;
import com.btg.core.dominio.excepcion.ExcepcionValorObligatorio;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ClienteTest {

    @Test
    public void crearClienteConDatosCompletosExitosamente() {
        // Arrange - Act
        Cliente cliente = new ClienteTestDataBuilder().construir();

        // Assert
        assertEquals("abc-123-def", cliente.getId());
        assertEquals("Juan Pérez", cliente.getNombre());
        assertEquals("juan.perez@email.com", cliente.getEmail());
        assertEquals("3001234567", cliente.getTelefono());
        assertEquals(500000.0, cliente.getSaldo(), 0.01);
        assertEquals("Clave123*", cliente.getContrasena());
        assertEquals("CLIENTE", cliente.getRol());
    }

    @Test
    public void crearClienteSinIdConSaldoInicialExitosamente() {
        // Arrange - Act
        Cliente cliente = new Cliente("María López", "maria@email.com", "3109876543", "MiClave123");

        // Assert
        assertNull(cliente.getId());
        assertEquals("María López", cliente.getNombre());
        assertEquals("maria@email.com", cliente.getEmail());
        assertEquals("3109876543", cliente.getTelefono());
        assertEquals(500000.0, cliente.getSaldo(), 0.01);
        assertEquals("MiClave123", cliente.getContrasena());
        assertEquals(Cliente.ROL_CLIENTE, cliente.getRol());
    }

    @Test
    public void crearClienteSinNombreLanzaExcepcion() {
        // Arrange - Act - Assert
        BasePrueba.assertThrows(
                () -> new ClienteTestDataBuilder().conNombre(null).construir(),
                ExcepcionValorObligatorio.class,
                "El nombre del cliente es obligatorio"
        );
    }

    @Test
    public void crearClienteSinEmailLanzaExcepcion() {
        // Arrange - Act - Assert
        BasePrueba.assertThrows(
                () -> new ClienteTestDataBuilder().conEmail(null).construir(),
                ExcepcionValorObligatorio.class,
                "El email del cliente es obligatorio"
        );
    }

    @Test
    public void crearClienteConEmailInvalidoLanzaExcepcion() {
        // Arrange - Act - Assert
        BasePrueba.assertThrows(
                () -> new ClienteTestDataBuilder().conEmail("correo-invalido").construir(),
                ExcepcionValorInvalido.class,
                "El email del cliente no tiene un formato válido"
        );
    }

    @Test
    public void crearClienteSinTelefonoLanzaExcepcion() {
        // Arrange - Act - Assert
        BasePrueba.assertThrows(
                () -> new ClienteTestDataBuilder().conTelefono(null).construir(),
                ExcepcionValorObligatorio.class,
                "El teléfono del cliente es obligatorio"
        );
    }

    @Test
    public void crearClienteConTelefonoNoNumericoLanzaExcepcion() {
        // Arrange - Act - Assert
        BasePrueba.assertThrows(
                () -> new ClienteTestDataBuilder().conTelefono("abc-no-numerico").construir(),
                ExcepcionValorInvalido.class,
                "El teléfono del cliente debe ser numérico"
        );
    }

    @Test
    public void crearClienteConNombreVacioLanzaExcepcion() {
        // Arrange - Act - Assert
        BasePrueba.assertThrows(
                () -> new ClienteTestDataBuilder().conNombre("").construir(),
                ExcepcionValorObligatorio.class,
                "El nombre del cliente es obligatorio"
        );
    }

    @Test
    public void crearClienteConEmailVacioLanzaExcepcion() {
        // Arrange - Act - Assert
        BasePrueba.assertThrows(
                () -> new ClienteTestDataBuilder().conEmail("").construir(),
                ExcepcionValorObligatorio.class,
                "El email del cliente es obligatorio"
        );
    }

    @Test
    public void crearClienteConTelefonoVacioLanzaExcepcion() {
        // Arrange - Act - Assert
        BasePrueba.assertThrows(
                () -> new ClienteTestDataBuilder().conTelefono("").construir(),
                ExcepcionValorObligatorio.class,
                "El teléfono del cliente es obligatorio"
        );
    }

    @Test
    public void crearClienteConCredencialesCompletasExitosamente() {
        // Arrange - Act
        Cliente cliente = new Cliente("Carlos García", "carlos@email.com", "3005551234", "Password123");

        // Assert
        assertEquals("Carlos García", cliente.getNombre());
        assertEquals("carlos@email.com", cliente.getEmail());
        assertEquals("Password123", cliente.getContrasena());
        assertEquals(Cliente.ROL_CLIENTE, cliente.getRol());
    }

    @Test
    public void crearClienteSinContrasenaLanzaExcepcion() {
        // Arrange - Act - Assert
        BasePrueba.assertThrows(
                () -> new Cliente("Juan Test", "juan@email.com", "3001234567", null),
                ExcepcionValorObligatorio.class,
                "La contraseña del cliente es obligatoria"
        );
    }

    @Test
    public void crearClienteConContrasenaVaciaLanzaExcepcion() {
        // Arrange - Act - Assert
        BasePrueba.assertThrows(
                () -> new Cliente("Juan Test", "juan@email.com", "3001234567", ""),
                ExcepcionValorObligatorio.class,
                "La contraseña del cliente es obligatoria"
        );
    }

    @Test
    public void crearClienteConConstructorCompletoAsignaRolCorrectamente() {
        // Arrange - Act
        Cliente cliente = new ClienteTestDataBuilder().conRol("ADMIN").construir();

        // Assert
        assertEquals("ADMIN", cliente.getRol());
    }
}
