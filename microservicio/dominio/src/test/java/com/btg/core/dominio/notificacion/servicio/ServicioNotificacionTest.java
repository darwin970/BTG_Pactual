package com.btg.core.dominio.notificacion.servicio;

import com.btg.core.dominio.cliente.modelo.dto.ClienteDTO;
import com.btg.core.dominio.cliente.puerto.dao.DaoCliente;
import com.btg.core.dominio.notificacion.modelo.dto.NotificacionDTO;
import com.btg.core.dominio.notificacion.puerto.RepositorioNotificacion;
import com.btg.core.dominio.notificacion.puerto.ServicioEnvioNotificacion;
import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServicioNotificacionTest {

    private static final String ID_CLIENTE = "cliente-001";
    private static final String EMAIL_CLIENTE = "cliente@btg.com";
    private static final String TELEFONO_CLIENTE = "3002000000";
    private static final String NOMBRE_FONDO = "FPV_BTG_PACTUAL_RECAUDADORA";

    private DaoCliente daoCliente;
    private ServicioEnvioNotificacion servicioEnvioNotificacion;
    private RepositorioNotificacion repositorioNotificacion;
    private ServicioNotificacion servicioNotificacion;

    @Before
    public void setUp() {
        daoCliente = mock(DaoCliente.class);
        servicioEnvioNotificacion = mock(ServicioEnvioNotificacion.class);
        repositorioNotificacion = mock(RepositorioNotificacion.class);
        servicioNotificacion = new ServicioNotificacion(daoCliente, servicioEnvioNotificacion, repositorioNotificacion);

        when(daoCliente.obtenerPorId(ID_CLIENTE)).thenReturn(
                new ClienteDTO(ID_CLIENTE, "Cliente BTG", EMAIL_CLIENTE, TELEFONO_CLIENTE, 500000.0, "CLIENTE"));
    }

    @Test
    public void notificacionExitosaEmailRetornaEstadoEnviado() {
        TransaccionDTO transaccion = new TransaccionDTO("txn-1", "1", NOMBRE_FONDO, ID_CLIENTE,
                75000.0, "ACTIVA", "EMAIL", "2024-01-01", "N/A");

        NotificacionDTO resultado = servicioNotificacion.ejecutar(transaccion);

        assertNotNull(resultado);
        assertEquals("ENVIADO", resultado.getEstado());
        assertEquals(EMAIL_CLIENTE, resultado.getDestinatario());
        assertEquals("EMAIL", resultado.getCanal());
        assertNull(resultado.getDetalleError());
        verify(repositorioNotificacion).guardar(any(NotificacionDTO.class));
    }

    @Test
    public void notificacionExitosaSmsUsaTelefono() {
        TransaccionDTO transaccion = new TransaccionDTO("txn-2", "1", NOMBRE_FONDO, ID_CLIENTE,
                75000.0, "ACTIVA", "SMS", "2024-01-01", "N/A");

        NotificacionDTO resultado = servicioNotificacion.ejecutar(transaccion);

        assertEquals(TELEFONO_CLIENTE, resultado.getDestinatario());
        assertEquals("SMS", resultado.getCanal());
        assertEquals("ENVIADO", resultado.getEstado());
        verify(servicioEnvioNotificacion).enviar(eq(TELEFONO_CLIENTE), eq("SMS"), anyString());
    }

    @Test
    public void mensajeConteneFondoYMontoParaSuscripcion() {
        TransaccionDTO transaccion = new TransaccionDTO("txn-3", "1", NOMBRE_FONDO, ID_CLIENTE,
                75000.0, "ACTIVA", "EMAIL", "2024-01-01", "N/A");

        NotificacionDTO resultado = servicioNotificacion.ejecutar(transaccion);

        assertNotNull(resultado.getMensaje());
        assertEquals(true, resultado.getMensaje().contains(NOMBRE_FONDO));
        assertEquals(true, resultado.getMensaje().contains("75000"));
    }

    @Test
    public void mensajeCancelacionContieneTextoAdecuado() {
        TransaccionDTO transaccion = new TransaccionDTO("txn-4", "1", NOMBRE_FONDO, ID_CLIENTE,
                75000.0, "CANCELADA", "EMAIL", "2024-01-01", "2024-01-02");

        NotificacionDTO resultado = servicioNotificacion.ejecutar(transaccion);

        assertNotNull(resultado.getMensaje());
        assertEquals(true, resultado.getMensaje().contains("cancelada"));
    }

    @Test
    public void falloEnvioRegistraTrazaErrorYRelanzaExcepcion() {
        TransaccionDTO transaccion = new TransaccionDTO("txn-5", "1", NOMBRE_FONDO, ID_CLIENTE,
                75000.0, "ACTIVA", "EMAIL", "2024-01-01", "N/A");
        doThrow(new RuntimeException("SMTP no disponible")).when(servicioEnvioNotificacion)
                .enviar(anyString(), anyString(), anyString());

        try {
            servicioNotificacion.ejecutar(transaccion);
        } catch (RuntimeException e) {
            assertEquals(true, e.getMessage().contains("SMTP no disponible"));
        }

        verify(repositorioNotificacion).guardar(any(NotificacionDTO.class));
    }
}
