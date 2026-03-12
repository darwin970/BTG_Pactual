package com.btg.core.infraestructura.transaccion.controlador;

import com.btg.core.aplicacion.transaccion.consulta.ManejadorConsultarTransacciones;
import com.btg.core.dominio.excepcion.ExcepcionAccesoDenegado;
import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clientes/{clienteId}/transacciones")
@RequiredArgsConstructor
public class ConsultaControladorTransaccion {

    private static final String ACCESO_DENEGADO = "No tiene permiso para operar sobre esta cuenta";

    private final ManejadorConsultarTransacciones manejadorConsultarTransacciones;

    @GetMapping
    public ResponseEntity<List<TransaccionDTO>> obtenerHistorial(@PathVariable String clienteId) {
        validarPropiedadRecurso(clienteId);
        return new ResponseEntity<>(manejadorConsultarTransacciones.ejecutar(clienteId), HttpStatus.OK);
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
