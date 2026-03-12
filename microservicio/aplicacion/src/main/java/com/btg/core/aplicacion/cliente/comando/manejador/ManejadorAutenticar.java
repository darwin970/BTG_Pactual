package com.btg.core.aplicacion.cliente.comando.manejador;

import com.btg.core.aplicacion.ComandoRespuesta;
import com.btg.core.aplicacion.cliente.comando.ComandoAutenticar;
import com.btg.core.aplicacion.manejador.ManejadorComandoRespuesta;
import com.btg.core.dominio.cliente.modelo.entidad.RespuestaAutenticacion;
import com.btg.core.dominio.cliente.servicio.ServicioAutenticar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManejadorAutenticar implements ManejadorComandoRespuesta<ComandoAutenticar, ComandoRespuesta<RespuestaAutenticacion>> {

    private final ServicioAutenticar servicioAutenticar;

    @Override
    public ComandoRespuesta<RespuestaAutenticacion> ejecutar(ComandoAutenticar comando) {
        RespuestaAutenticacion respuesta = servicioAutenticar.ejecutar(comando.getEmail(), comando.getContrasena());
        return new ComandoRespuesta<>(respuesta);
    }
}
