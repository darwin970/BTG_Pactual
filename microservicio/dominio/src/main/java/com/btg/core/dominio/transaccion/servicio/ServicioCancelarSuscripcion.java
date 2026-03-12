package com.btg.core.dominio.transaccion.servicio;

import com.btg.core.dominio.cliente.modelo.dto.ClienteDTO;
import com.btg.core.dominio.cliente.puerto.dao.DaoCliente;
import com.btg.core.dominio.excepcion.ExcepcionSinDatos;
import com.btg.core.dominio.excepcion.ExcepcionValorInvalido;
import com.btg.core.dominio.fondo.modelo.dto.FondoDTO;
import com.btg.core.dominio.fondo.puerto.dao.DaoFondo;
import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import com.btg.core.dominio.transaccion.puerto.dao.DaoTransaccion;
import com.btg.core.dominio.transaccion.puerto.repositorio.RepositorioTransaccion;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ServicioCancelarSuscripcion {

    private static final String ESTADO_ACTIVA = "ACTIVA";
    private static final String ESTADO_CANCELADA = "CANCELADA";
    private static final String LA_TRANSACCION_NO_EXISTE = "La transacción no existe";
    private static final String EL_CLIENTE_NO_EXISTE = "El cliente no existe";
    private static final String FONDO_NO_ENCONTRADO = "Fondo no encontrado";
    private static final String NO_TIENE_SUSCRIPCION_ACTIVA = "No tiene suscripción activa al fondo %s";
    private static final String TRANSACCION_NO_PERTENECE_AL_CLIENTE = "La transacción no pertenece al cliente";

    private final DaoTransaccion daoTransaccion;
    private final DaoCliente daoCliente;
    private final DaoFondo daoFondo;
    private final RepositorioTransaccion repositorioTransaccion;

    public TransaccionDTO ejecutar(String transaccionId, String clienteId) {
        TransaccionDTO transaccion = daoTransaccion.obtenerPorId(transaccionId);
        if (transaccion == null) {
            throw new ExcepcionSinDatos(LA_TRANSACCION_NO_EXISTE);
        }

        if (!transaccion.getClienteId().equals(clienteId)) {
            throw new ExcepcionValorInvalido(TRANSACCION_NO_PERTENECE_AL_CLIENTE);
        }

        FondoDTO fondo = daoFondo.obtenerPorId(transaccion.getFondoId());
        if (fondo == null) {
            throw new ExcepcionSinDatos(FONDO_NO_ENCONTRADO);
        }

        if (!ESTADO_ACTIVA.equals(transaccion.getEstado())) {
            throw new ExcepcionValorInvalido(String.format(NO_TIENE_SUSCRIPCION_ACTIVA, fondo.getNombre()));
        }

        ClienteDTO cliente = daoCliente.obtenerPorId(clienteId);
        if (cliente == null) {
            throw new ExcepcionSinDatos(EL_CLIENTE_NO_EXISTE);
        }

        Double nuevoSaldo = cliente.getSaldo() + transaccion.getMonto();
        String fechaCancelacion = LocalDateTime.now().toString();

        repositorioTransaccion.cancelar(transaccionId, fechaCancelacion, clienteId, nuevoSaldo);

        return new TransaccionDTO(
                transaccion.getId(),
                transaccion.getFondoId(),
                fondo.getNombre(),
                transaccion.getClienteId(),
                transaccion.getMonto(),
                ESTADO_CANCELADA,
                transaccion.getPreferenciaNotificacion(),
                transaccion.getFecha(),
                fechaCancelacion
        );
    }
}
