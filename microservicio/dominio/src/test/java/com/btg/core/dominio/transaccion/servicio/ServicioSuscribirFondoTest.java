package com.btg.core.dominio.transaccion.servicio;

import com.btg.core.BasePrueba;
import com.btg.core.dominio.cliente.modelo.dto.ClienteDTO;
import com.btg.core.dominio.cliente.puerto.dao.DaoCliente;
import com.btg.core.dominio.excepcion.ExcepcionSinDatos;
import com.btg.core.dominio.excepcion.ExcepcionValorInvalido;
import com.btg.core.dominio.fondo.modelo.dto.FondoDTO;
import com.btg.core.dominio.fondo.puerto.dao.DaoFondo;
import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import com.btg.core.dominio.transaccion.modelo.entidad.Transaccion;
import com.btg.core.dominio.transaccion.modelo.entidad.TransaccionTestDataBuilder;
import com.btg.core.dominio.transaccion.puerto.repositorio.RepositorioTransaccion;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServicioSuscribirFondoTest {

    private static final String ID_CLIENTE = "cliente-001";
    private static final String ID_FONDO = "1";
    private static final String NOMBRE_FONDO = "FPV_BTG_PACTUAL_RECAUDADORA";
    private static final Double SALDO_CLIENTE = 500000.0;
    private static final Double MONTO_MINIMO = 75000.0;

    private DaoCliente daoCliente;
    private DaoFondo daoFondo;
    private RepositorioTransaccion repositorioTransaccion;
    private ServicioSuscribirFondo servicioSuscribirFondo;

    @Before
    public void setUp() {
        daoCliente = mock(DaoCliente.class);
        daoFondo = mock(DaoFondo.class);
        repositorioTransaccion = mock(RepositorioTransaccion.class);
        servicioSuscribirFondo = new ServicioSuscribirFondo(daoCliente, daoFondo, repositorioTransaccion);
    }

    private ClienteDTO crearClienteDTO(Double saldo) {
        return new ClienteDTO(ID_CLIENTE, "Cliente BTG", "cliente@btg.com",
                "3002000000", saldo, "CLIENTE");
    }

    private FondoDTO crearFondoDTO() {
        return new FondoDTO(ID_FONDO, NOMBRE_FONDO, MONTO_MINIMO, "FPV");
    }

    private void configurarMocksExitoso(Double saldoCliente) {
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(crearClienteDTO(saldoCliente));
        when(daoFondo.obtenerPorId(ID_FONDO)).thenReturn(crearFondoDTO());
        when(repositorioTransaccion.crear(any(Transaccion.class), anyString(), anyDouble()))
                .thenReturn("txn-001");
    }

    @Test
    public void suscripcionExitosaRetornaTransaccionDTO() {
        // Arrange
        Transaccion transaccion = new TransaccionTestDataBuilder().construir();
        configurarMocksExitoso(SALDO_CLIENTE);
        when(repositorioTransaccion.crear(any(Transaccion.class), anyString(), anyDouble()))
                .thenReturn("txn-001");

        // Act
        TransaccionDTO resultado = servicioSuscribirFondo.ejecutar(transaccion);

        // Assert
        assertNotNull(resultado);
        assertEquals("txn-001", resultado.getId());
        assertEquals("1", resultado.getFondoId());
        assertEquals("FPV_BTG_PACTUAL_RECAUDADORA", resultado.getNombreFondo());
        assertEquals("cliente-001", resultado.getClienteId());
        assertEquals(75000.0, resultado.getMonto(), 0.01);
        assertEquals("ACTIVA", resultado.getEstado());
        assertEquals("EMAIL", resultado.getPreferenciaNotificacion());
        verify(repositorioTransaccion).crear(transaccion, "cliente-001", 425000.0);
    }

    @Test
    public void suscripcionConClienteInexistenteLanzaExcepcion() {
        // Arrange
        Transaccion transaccion = new TransaccionTestDataBuilder().construir();
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(null);

        // Act - Assert
        BasePrueba.assertThrows(
                () -> servicioSuscribirFondo.ejecutar(transaccion),
                ExcepcionSinDatos.class,
                "El cliente no existe"
        );
    }

    @Test
    public void suscripcionConFondoInexistenteLanzaExcepcion() {
        // Arrange
        Transaccion transaccion = new TransaccionTestDataBuilder().construir();
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(crearClienteDTO(SALDO_CLIENTE));
        when(daoFondo.obtenerPorId(ID_FONDO)).thenReturn(null);

        // Act - Assert
        BasePrueba.assertThrows(
                () -> servicioSuscribirFondo.ejecutar(transaccion),
                ExcepcionSinDatos.class,
                "Fondo no encontrado"
        );
    }

    @Test
    public void suscripcionConSaldoInsuficienteLanzaExcepcion() {
        // Arrange
        Transaccion transaccion = new TransaccionTestDataBuilder().conMonto(600000.0).construir();
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(crearClienteDTO(SALDO_CLIENTE));
        when(daoFondo.obtenerPorId(ID_FONDO)).thenReturn(crearFondoDTO());

        // Act - Assert
        BasePrueba.assertThrows(
                () -> servicioSuscribirFondo.ejecutar(transaccion),
                ExcepcionValorInvalido.class,
                "No tiene saldo disponible para vincularse al fondo " + NOMBRE_FONDO
        );
    }

    @Test
    public void suscripcionConMontoMenorAlMinimoLanzaExcepcion() {
        // Arrange
        Transaccion transaccion = new TransaccionTestDataBuilder().conMonto(50000.0).construir();
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(crearClienteDTO(SALDO_CLIENTE));
        when(daoFondo.obtenerPorId(ID_FONDO)).thenReturn(crearFondoDTO());

        // Act - Assert
        BasePrueba.assertThrows(
                () -> servicioSuscribirFondo.ejecutar(transaccion),
                ExcepcionValorInvalido.class,
                "El monto no alcanza el mínimo de vinculación al fondo " + NOMBRE_FONDO
        );
    }

    @Test
    public void suscripcionCalculaNuevoSaldoCorrectamente() {
        // Arrange
        Transaccion transaccion = new TransaccionTestDataBuilder().conMonto(75000.0).construir();
        configurarMocksExitoso(SALDO_CLIENTE);

        // Act
        servicioSuscribirFondo.ejecutar(transaccion);

        // Assert
        verify(repositorioTransaccion).crear(transaccion, ID_CLIENTE, 425000.0);
    }

    @Test
    public void suscripcionMultipleFondosDescuentoAcumulativo() {
        // Arrange — primera suscripción
        Transaccion transaccion1 = new TransaccionTestDataBuilder()
                .conId("txn-001").conMonto(75000.0).construir();
        configurarMocksExitoso(SALDO_CLIENTE);

        // Act
        servicioSuscribirFondo.ejecutar(transaccion1);
        verify(repositorioTransaccion).crear(transaccion1, ID_CLIENTE, 425000.0);

        // Arrange — segunda suscripción con saldo ya reducido
        Transaccion transaccion2 = new TransaccionTestDataBuilder()
                .conId("txn-002").conFondoId("3").conMonto(50000.0).construir();
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(crearClienteDTO(425000.0));
        when(daoFondo.obtenerPorId("3")).thenReturn(new FondoDTO("3", "DEUDAPRIVADA", 50000.0, "FIC"));

        // Act
        servicioSuscribirFondo.ejecutar(transaccion2);

        // Assert
        verify(repositorioTransaccion).crear(transaccion2, ID_CLIENTE, 375000.0);
    }
}
