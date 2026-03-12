package com.btg.core.aplicacion.transaccion.comando.fabrica;

import com.btg.core.aplicacion.transaccion.comando.ComandoSuscripcion;
import com.btg.core.dominio.transaccion.modelo.entidad.Transaccion;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class FabricaCrearSuscripcion {

    public Transaccion ejecutar(ComandoSuscripcion comando) {
        return new Transaccion(
                UUID.randomUUID().toString(),
                comando.getFondoId(),
                comando.getClienteId(),
                comando.getMonto(),
                "ACTIVA",
                comando.getPreferenciaNotificacion(),
                LocalDateTime.now()
        );
    }
}
