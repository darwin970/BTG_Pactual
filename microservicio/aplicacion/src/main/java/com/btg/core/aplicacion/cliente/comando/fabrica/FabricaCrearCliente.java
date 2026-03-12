package com.btg.core.aplicacion.cliente.comando.fabrica;

import com.btg.core.aplicacion.cliente.comando.ComandoCliente;
import com.btg.core.dominio.cliente.modelo.entidad.Cliente;
import org.springframework.stereotype.Component;

@Component
public class FabricaCrearCliente {

    public Cliente ejecutar(ComandoCliente comando) {
        return new Cliente(comando.getNombre(), comando.getEmail(), comando.getTelefono());
    }
}
