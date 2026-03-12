package com.btg.core.infraestructura.cliente.controlador;

import com.btg.core.aplicacion.ComandoRespuesta;
import com.btg.core.aplicacion.cliente.comando.ComandoAutenticar;
import com.btg.core.aplicacion.cliente.comando.manejador.ManejadorAutenticar;
import com.btg.core.dominio.cliente.modelo.entidad.RespuestaAutenticacion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ControladorAutenticacion {

    private final ManejadorAutenticar manejadorAutenticar;

    @PostMapping("/login")
    public ResponseEntity<ComandoRespuesta<RespuestaAutenticacion>> login(
            @RequestBody ComandoAutenticar comando) {
        return ResponseEntity.ok(manejadorAutenticar.ejecutar(comando));
    }
}
