package com.btg.core.aplicacion.transaccion.comando.manejador;

import com.btg.core.aplicacion.ComandoRespuesta;
import com.btg.core.aplicacion.manejador.ManejadorComandoRespuesta;
import com.btg.core.aplicacion.transaccion.comando.ComandoSuscripcion;
import com.btg.core.aplicacion.transaccion.comando.fabrica.FabricaCrearSuscripcion;
import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import com.btg.core.dominio.transaccion.modelo.entidad.Transaccion;
import com.btg.core.dominio.transaccion.servicio.ServicioSuscribirFondo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManejadorSuscribirFondo implements ManejadorComandoRespuesta<ComandoSuscripcion, ComandoRespuesta<TransaccionDTO>> {

    private final FabricaCrearSuscripcion fabricaCrearSuscripcion;
    private final ServicioSuscribirFondo servicioSuscribirFondo;

    @Override
    public ComandoRespuesta<TransaccionDTO> ejecutar(ComandoSuscripcion comando) {
        Transaccion transaccion = fabricaCrearSuscripcion.ejecutar(comando);
        return new ComandoRespuesta<>(servicioSuscribirFondo.ejecutar(transaccion));
    }
}
