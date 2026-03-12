package com.btg.core.infraestructura.cliente.controlador;

import com.btg.core.aplicacion.ComandoRespuesta;
import com.btg.core.aplicacion.cliente.comando.ComandoCliente;
import com.btg.core.aplicacion.cliente.comando.manejador.ManejadorCrearCliente;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ComandoControladorCliente {

    private final ManejadorCrearCliente manejadorCrearCliente;

    @PostMapping
    public ResponseEntity<ComandoRespuesta<String>> crear(@RequestBody ComandoCliente comando) {
        return new ResponseEntity<>(manejadorCrearCliente.ejecutar(comando), HttpStatus.CREATED);
    }
}
