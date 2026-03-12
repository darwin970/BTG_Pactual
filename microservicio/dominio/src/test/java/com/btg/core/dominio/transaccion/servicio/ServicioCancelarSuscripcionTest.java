package com.btg.core.dominio.transaccion.servicio;

import com.btg.core.BasePrueba;
import com.btg.core.dominio.cliente.modelo.dto.ClienteDTO;
import com.btg.core.dominio.cliente.puerto.dao.DaoCliente;
import com.btg.core.dominio.excepcion.ExcepcionSinDatos;
import com.btg.core.dominio.excepcion.ExcepcionValorInvalido;
import com.btg.core.dominio.fondo.modelo.dto.FondoDTO;
import com.btg.core.dominio.fondo.puerto.dao.DaoFondo;
import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import com.btg.core.dominio.transaccion.puerto.dao.DaoTransaccion;
import com.btg.core.dominio.transaccion.puerto.repositorio.RepositorioTransaccion;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServicioCancelarSuscripcionTest {

    private static final String ID_TRANSACCION = "txn-001";
    private static final String ID_CLIENTE = "cliente-001";
    private static final String ID_FONDO = "1";
    private static final String NOMBRE_FONDO = "FPV_BTG_PACTUAL_RECAUDADORA";
    private static final Double SALDO_CLIENTE = 425000.0;
    private static final Double MONTO_SUSCRIPCION = 75000.0;

    private DaoTransaccion daoTransaccion;
    private DaoCliente daoCliente;
    private DaoFondo daoFondo;
    private RepositorioTransaccion repositorioTransaccion;
    private ServicioCancelarSuscripcion servicioCancelarSuscripcion;

    @Before
    public void setUp() {
        daoTransaccion = mock(DaoTransaccion.class);
        daoCliente = mock(DaoCliente.class);
        daoFondo = mock(DaoFondo.class);
        repositorioTransaccion = mock(RepositorioTransaccion.class);
        servicioCancelarSuscripcion = new ServicioCancelarSuscripcion(
                daoTransaccion, daoCliente, daoFondo, repositorioTransaccion);
    }

    private TransaccionDTO crearTransaccionDTO(String estado) {
        return new TransaccionDTO(ID_TRANSACCION, ID_FONDO, NOMBRE_FONDO, ID_CLIENTE,
                MONTO_SUSCRIPCION, estado, "EMAIL", "2026-03-12T10:00:00", null);
    }

    private ClienteDTO crearClienteDTO() {
        return new ClienteDTO(ID_CLIENTE, "Cliente BTG", "cliente@btg.com",
                "3002000000", SALDO_CLIENTE, "CLIENTE");
    }

    private FondoDTO crearFondoDTO() {
        return new FondoDTO(ID_FONDO, NOMBRE_FONDO, 75000.0, "FPV");
    }

    private void configurarMocksExitoso() {
        when(daoTransaccion.obtenerPorId(ID_TRANSACCION)).thenReturn(crearTransaccionDTO("ACTIVA"));
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(crearClienteDTO());
        when(daoFondo.obtenerPorId(ID_FONDO)).thenReturn(crearFondoDTO());
    }

    @Test
    public void cancelacionExitosaRetornaTransaccionCancelada() {
        // Arrange
        configurarMocksExitoso();

        // Act
        TransaccionDTO resultado = servicioCancelarSuscripcion.ejecutar(ID_TRANSACCION, ID_CLIENTE);

        // Assert
        assertNotNull(resultado);
        assertEquals(ID_TRANSACCION, resultado.getId());
        assertEquals("CANCELADA", resultado.getEstado());
        assertNotNull(resultado.getFechaCancelacion());
        assertEquals(NOMBRE_FONDO, resultado.getNombreFondo());
    }

    @Test
    public void cancelacionExitosaRestauraSaldo() {
        // Arrange
        configurarMocksExitoso();

        // Act
        servicioCancelarSuscripcion.ejecutar(ID_TRANSACCION, ID_CLIENTE);

        // Assert
        verify(repositorioTransaccion).cancelar(eq(ID_TRANSACCION), anyString(), eq(ID_CLIENTE), eq(500000.0));
    }

    @Test
    public void cancelacionConTransaccionInexistenteLanzaExcepcion() {
        // Arrange
        when(daoTransaccion.obtenerPorId(ID_TRANSACCION)).thenReturn(null);

        // Act - Assert
        BasePrueba.assertThrows(
                () -> servicioCancelarSuscripcion.ejecutar(ID_TRANSACCION, ID_CLIENTE),
                ExcepcionSinDatos.class,
                "La transacción no existe"
        );
    }

    @Test
    public void cancelacionConTransaccionDeOtroClienteLanzaExcepcion() {
        // Arrange
        when(daoTransaccion.obtenerPorId(ID_TRANSACCION)).thenReturn(crearTransaccionDTO("ACTIVA"));

        // Act - Assert
        BasePrueba.assertThrows(
                () -> servicioCancelarSuscripcion.ejecutar(ID_TRANSACCION, "otro-cliente"),
                ExcepcionValorInvalido.class,
                "La transacción no pertenece al cliente"
        );
    }

    @Test
    public void cancelacionConTransaccionYaCanceladaLanzaExcepcion() {
        // Arrange
        when(daoTransaccion.obtenerPorId(ID_TRANSACCION)).thenReturn(crearTransaccionDTO("CANCELADA"));
        when(daoFondo.obtenerPorId(ID_FONDO)).thenReturn(crearFondoDTO());

        // Act - Assert
        BasePrueba.assertThrows(
                () -> servicioCancelarSuscripcion.ejecutar(ID_TRANSACCION, ID_CLIENTE),
                ExcepcionValorInvalido.class,
                "No tiene suscripción activa al fondo " + NOMBRE_FONDO
        );
    }

    @Test
    public void cancelacionConFondoInexistenteLanzaExcepcion() {
        // Arrange
        when(daoTransaccion.obtenerPorId(ID_TRANSACCION)).thenReturn(crearTransaccionDTO("ACTIVA"));
        when(daoFondo.obtenerPorId(ID_FONDO)).thenReturn(null);

        // Act - Assert
        BasePrueba.assertThrows(
                () -> servicioCancelarSuscripcion.ejecutar(ID_TRANSACCION, ID_CLIENTE),
                ExcepcionSinDatos.class,
                "Fondo no encontrado"
        );
    }

    @Test
    public void cancelacionConClienteInexistenteLanzaExcepcion() {
        // Arrange
        when(daoTransaccion.obtenerPorId(ID_TRANSACCION)).thenReturn(crearTransaccionDTO("ACTIVA"));
        when(daoFondo.obtenerPorId(ID_FONDO)).thenReturn(crearFondoDTO());
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(null);

        // Act - Assert
        BasePrueba.assertThrows(
                () -> servicioCancelarSuscripcion.ejecutar(ID_TRANSACCION, ID_CLIENTE),
                ExcepcionSinDatos.class,
                "El cliente no existe"
        );
    }
}
