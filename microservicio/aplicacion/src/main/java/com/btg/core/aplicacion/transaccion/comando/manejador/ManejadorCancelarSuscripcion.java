package com.btg.core.aplicacion.transaccion.comando.manejador;

import com.btg.core.aplicacion.ComandoRespuesta;
import com.btg.core.aplicacion.manejador.ManejadorComandoRespuesta;
import com.btg.core.aplicacion.transaccion.comando.ComandoCancelacion;
import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import com.btg.core.dominio.transaccion.servicio.ServicioCancelarSuscripcion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManejadorCancelarSuscripcion implements ManejadorComandoRespuesta<ComandoCancelacion, ComandoRespuesta<TransaccionDTO>> {

    private final ServicioCancelarSuscripcion servicioCancelarSuscripcion;

    @Override
    public ComandoRespuesta<TransaccionDTO> ejecutar(ComandoCancelacion comando) {
        return new ComandoRespuesta<>(
                servicioCancelarSuscripcion.ejecutar(comando.getTransaccionId(), comando.getClienteId()));
    }
}
