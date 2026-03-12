package com.btg.core.infraestructura.transaccion.controlador;

import com.btg.core.aplicacion.ComandoRespuesta;
import com.btg.core.aplicacion.transaccion.comando.ComandoCancelacion;
import com.btg.core.aplicacion.transaccion.comando.ComandoSuscripcion;
import com.btg.core.aplicacion.transaccion.comando.manejador.ManejadorCancelarSuscripcion;
import com.btg.core.aplicacion.transaccion.comando.manejador.ManejadorSuscribirFondo;
import com.btg.core.dominio.excepcion.ExcepcionAccesoDenegado;
import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes/{clienteId}/suscripciones")
@RequiredArgsConstructor
public class ComandoControladorTransaccion {

    private static final String ACCESO_DENEGADO = "No tiene permiso para operar sobre esta cuenta";

    private final ManejadorSuscribirFondo manejadorSuscribirFondo;
    private final ManejadorCancelarSuscripcion manejadorCancelarSuscripcion;

    @PostMapping
    public ResponseEntity<ComandoRespuesta<TransaccionDTO>> suscribir(
            @PathVariable String clienteId,
            @RequestBody ComandoSuscripcion comando) {

        validarPropiedadRecurso(clienteId);
        comando.setClienteId(clienteId);

        return new ResponseEntity<>(manejadorSuscribirFondo.ejecutar(comando), HttpStatus.CREATED);
    }

    @DeleteMapping("/{transaccionId}")
    public ResponseEntity<ComandoRespuesta<TransaccionDTO>> cancelar(
            @PathVariable String clienteId,
            @PathVariable String transaccionId) {

        validarPropiedadRecurso(clienteId);
        ComandoCancelacion comando = new ComandoCancelacion(clienteId, transaccionId);

        return new ResponseEntity<>(manejadorCancelarSuscripcion.ejecutar(comando), HttpStatus.OK);
    }

    private void validarPropiedadRecurso(String clienteId) {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        String clienteIdAutenticado = (String) autenticacion.getPrincipal();

        boolean esAdmin = autenticacion.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin && !clienteId.equals(clienteIdAutenticado)) {
            throw new ExcepcionAccesoDenegado(ACCESO_DENEGADO);
        }
    }
}
