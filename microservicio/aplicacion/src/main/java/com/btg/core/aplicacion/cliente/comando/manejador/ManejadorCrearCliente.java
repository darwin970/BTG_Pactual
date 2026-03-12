package com.btg.core.aplicacion.cliente.comando.manejador;

import com.btg.core.aplicacion.ComandoRespuesta;
import com.btg.core.aplicacion.cliente.comando.ComandoCliente;
import com.btg.core.aplicacion.cliente.comando.fabrica.FabricaCrearCliente;
import com.btg.core.aplicacion.manejador.ManejadorComandoRespuesta;
import com.btg.core.dominio.cliente.modelo.entidad.Cliente;
import com.btg.core.dominio.cliente.servicio.ServicioCrearCliente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManejadorCrearCliente implements ManejadorComandoRespuesta<ComandoCliente, ComandoRespuesta<String>> {

    private final FabricaCrearCliente fabricaCrearCliente;
    private final ServicioCrearCliente servicioCrearCliente;

    @Override
    public ComandoRespuesta<String> ejecutar(ComandoCliente comando) {
        Cliente cliente = fabricaCrearCliente.ejecutar(comando);
        return new ComandoRespuesta<>(servicioCrearCliente.ejecutar(cliente));
    }
}
