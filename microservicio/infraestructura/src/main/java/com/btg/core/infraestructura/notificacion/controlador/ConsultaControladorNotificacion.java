package com.btg.core.infraestructura.notificacion.controlador;

import com.btg.core.aplicacion.notificacion.consulta.ManejadorConsultarNotificaciones;
import com.btg.core.dominio.excepcion.ExcepcionAccesoDenegado;
import com.btg.core.dominio.notificacion.modelo.dto.NotificacionDTO;
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
@RequestMapping("/clientes/{clienteId}/notificaciones")
@RequiredArgsConstructor
public class ConsultaControladorNotificacion {

    private static final String ACCESO_DENEGADO = "No tiene permiso para operar sobre esta cuenta";

    private final ManejadorConsultarNotificaciones manejadorConsultarNotificaciones;

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> obtenerNotificaciones(@PathVariable String clienteId) {
        validarPropiedadRecurso(clienteId);
        return new ResponseEntity<>(manejadorConsultarNotificaciones.ejecutar(clienteId), HttpStatus.OK);
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
