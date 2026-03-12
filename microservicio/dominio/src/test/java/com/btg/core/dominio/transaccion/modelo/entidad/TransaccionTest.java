package com.btg.core.dominio.transaccion.modelo.entidad;

import com.btg.core.BasePrueba;
import com.btg.core.dominio.excepcion.ExcepcionValorInvalido;
import com.btg.core.dominio.excepcion.ExcepcionValorObligatorio;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransaccionTest {

    @Test
    public void crearTransaccionConDatosCompletosExitosamente() {
        // Arrange - Act
        Transaccion transaccion = new TransaccionTestDataBuilder().construir();

        // Assert
        assertEquals("txn-001", transaccion.getId());
        assertEquals("1", transaccion.getFondoId());
        assertEquals("cliente-001", transaccion.getClienteId());
        assertEquals(75000.0, transaccion.getMonto(), 0.01);
        assertEquals("ACTIVA", transaccion.getEstado());
        assertEquals("EMAIL", transaccion.getPreferenciaNotificacion());
    }

    @Test
    public void crearTransaccionConPreferenciaSmsExitosamente() {
        // Arrange - Act
        Transaccion transaccion = new TransaccionTestDataBuilder()
                .conPreferenciaNotificacion("SMS").construir();

        // Assert
        assertEquals("SMS", transaccion.getPreferenciaNotificacion());
    }

    @Test
    public void crearTransaccionSinIdLanzaExcepcion() {
        BasePrueba.assertThrows(
                () -> new TransaccionTestDataBuilder().conId(null).construir(),
                ExcepcionValorObligatorio.class,
                "El id de la transacción es obligatorio"
        );
    }

    @Test
    public void crearTransaccionSinFondoIdLanzaExcepcion() {
        BasePrueba.assertThrows(
                () -> new TransaccionTestDataBuilder().conFondoId(null).construir(),
                ExcepcionValorObligatorio.class,
                "El id del fondo es obligatorio"
        );
    }

    @Test
    public void crearTransaccionSinClienteIdLanzaExcepcion() {
        BasePrueba.assertThrows(
                () -> new TransaccionTestDataBuilder().conClienteId(null).construir(),
                ExcepcionValorObligatorio.class,
                "El id del cliente es obligatorio"
        );
    }

    @Test
    public void crearTransaccionSinMontoLanzaExcepcion() {
        BasePrueba.assertThrows(
                () -> new TransaccionTestDataBuilder().conMonto(null).construir(),
                ExcepcionValorObligatorio.class,
                "El monto de la transacción es obligatorio"
        );
    }

    @Test
    public void crearTransaccionConMontoNegativoLanzaExcepcion() {
        BasePrueba.assertThrows(
                () -> new TransaccionTestDataBuilder().conMonto(-1000.0).construir(),
                ExcepcionValorInvalido.class,
                "El monto de la transacción debe ser positivo"
        );
    }

    @Test
    public void crearTransaccionConMontoCeroLanzaExcepcion() {
        BasePrueba.assertThrows(
                () -> new TransaccionTestDataBuilder().conMonto(0.0).construir(),
                ExcepcionValorInvalido.class,
                "El monto de la transacción debe ser positivo"
        );
    }

    @Test
    public void crearTransaccionSinEstadoLanzaExcepcion() {
        BasePrueba.assertThrows(
                () -> new TransaccionTestDataBuilder().conEstado(null).construir(),
                ExcepcionValorObligatorio.class,
                "El estado de la transacción es obligatorio"
        );
    }

    @Test
    public void crearTransaccionSinPreferenciaNotificacionLanzaExcepcion() {
        BasePrueba.assertThrows(
                () -> new TransaccionTestDataBuilder().conPreferenciaNotificacion(null).construir(),
                ExcepcionValorObligatorio.class,
                "La preferencia de notificación es obligatoria"
        );
    }

    @Test
    public void crearTransaccionConPreferenciaNotificacionInvalidaLanzaExcepcion() {
        BasePrueba.assertThrows(
                () -> new TransaccionTestDataBuilder().conPreferenciaNotificacion("WHATSAPP").construir(),
                ExcepcionValorInvalido.class,
                "La preferencia de notificación debe ser EMAIL o SMS"
        );
    }

    @Test
    public void crearTransaccionSinFechaLanzaExcepcion() {
        BasePrueba.assertThrows(
                () -> new TransaccionTestDataBuilder().conFecha(null).construir(),
                ExcepcionValorObligatorio.class,
                "La fecha de la transacción es obligatoria"
        );
    }
}
