package com.btg.core.dominio.transaccion.servicio;

import com.btg.core.BasePrueba;
import com.btg.core.dominio.cliente.modelo.dto.ClienteDTO;
import com.btg.core.dominio.cliente.puerto.dao.DaoCliente;
import com.btg.core.dominio.excepcion.ExcepcionSinDatos;
import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import com.btg.core.dominio.transaccion.puerto.dao.DaoTransaccion;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServicioConsultarTransaccionesTest {

    private static final String ID_CLIENTE = "cliente-001";

    private DaoCliente daoCliente;
    private DaoTransaccion daoTransaccion;
    private ServicioConsultarTransacciones servicioConsultarTransacciones;

    @Before
    public void setUp() {
        daoCliente = mock(DaoCliente.class);
        daoTransaccion = mock(DaoTransaccion.class);
        servicioConsultarTransacciones = new ServicioConsultarTransacciones(daoCliente, daoTransaccion);
    }

    @Test
    public void consultaExitosaRetornaListaOrdenada() {
        ClienteDTO cliente = new ClienteDTO(ID_CLIENTE, "Test", "t@e.com", "300", 500000.0, "CLIENTE");
        TransaccionDTO txn1 = new TransaccionDTO("txn-1", "1", null, ID_CLIENTE, 75000.0, "ACTIVA", "EMAIL", "2026-03-10T10:00:00", null);
        TransaccionDTO txn2 = new TransaccionDTO("txn-2", "2", null, ID_CLIENTE, 50000.0, "CANCELADA", "SMS", "2026-03-11T12:00:00", "2026-03-12T08:00:00");
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(cliente);
        when(daoTransaccion.obtenerPorClienteId(ID_CLIENTE)).thenReturn(Arrays.asList(txn1, txn2));

        List<TransaccionDTO> resultado = servicioConsultarTransacciones.ejecutar(ID_CLIENTE);

        assertEquals(2, resultado.size());
        assertEquals("txn-2", resultado.get(0).getId()); // más reciente primero
        assertEquals("txn-1", resultado.get(1).getId());
    }

    @Test
    public void consultaMapea_N_A_CuandoFechaCancelacionEsNull() {
        ClienteDTO cliente = new ClienteDTO(ID_CLIENTE, "Test", "t@e.com", "300", 500000.0, "CLIENTE");
        TransaccionDTO txn = new TransaccionDTO("txn-1", "1", null, ID_CLIENTE, 75000.0, "ACTIVA", "EMAIL", "2026-03-10T10:00:00", null);
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(cliente);
        when(daoTransaccion.obtenerPorClienteId(ID_CLIENTE)).thenReturn(Collections.singletonList(txn));

        List<TransaccionDTO> resultado = servicioConsultarTransacciones.ejecutar(ID_CLIENTE);

        assertEquals("N/A", resultado.get(0).getFechaCancelacion());
    }

    @Test
    public void consultaConservaFechaCancelacionCuandoNoEsNull() {
        ClienteDTO cliente = new ClienteDTO(ID_CLIENTE, "Test", "t@e.com", "300", 500000.0, "CLIENTE");
        TransaccionDTO txn = new TransaccionDTO("txn-1", "1", null, ID_CLIENTE, 75000.0, "CANCELADA", "EMAIL", "2026-03-10T10:00:00", "2026-03-12T08:00:00");
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(cliente);
        when(daoTransaccion.obtenerPorClienteId(ID_CLIENTE)).thenReturn(Collections.singletonList(txn));

        List<TransaccionDTO> resultado = servicioConsultarTransacciones.ejecutar(ID_CLIENTE);

        assertEquals("2026-03-12T08:00:00", resultado.get(0).getFechaCancelacion());
    }

    @Test
    public void consultaListaVaciaRetornaListaVacia() {
        ClienteDTO cliente = new ClienteDTO(ID_CLIENTE, "Test", "t@e.com", "300", 500000.0, "CLIENTE");
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(cliente);
        when(daoTransaccion.obtenerPorClienteId(ID_CLIENTE)).thenReturn(Collections.emptyList());

        List<TransaccionDTO> resultado = servicioConsultarTransacciones.ejecutar(ID_CLIENTE);

        assertTrue(resultado.isEmpty());
    }

    @Test
    public void clienteInexistenteLanzaExcepcionSinDatos() {
        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(null);

        BasePrueba.assertThrows(
                () -> servicioConsultarTransacciones.ejecutar(ID_CLIENTE),
                ExcepcionSinDatos.class,
                "El cliente no existe");
    }
}
