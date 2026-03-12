package com.btg.core.dominio.transaccion.servicio;

import com.btg.core.dominio.cliente.modelo.dto.ClienteDTO;
import com.btg.core.dominio.cliente.puerto.dao.DaoCliente;
import com.btg.core.dominio.excepcion.ExcepcionSinDatos;
import com.btg.core.dominio.excepcion.ExcepcionValorInvalido;
import com.btg.core.dominio.fondo.modelo.dto.FondoDTO;
import com.btg.core.dominio.fondo.puerto.dao.DaoFondo;
import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import com.btg.core.dominio.transaccion.modelo.entidad.Transaccion;
import com.btg.core.dominio.transaccion.puerto.repositorio.RepositorioTransaccion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServicioSuscribirFondo {

    private static final String EL_CLIENTE_NO_EXISTE = "El cliente no existe";
    private static final String FONDO_NO_ENCONTRADO = "Fondo no encontrado";
    private static final String NO_TIENE_SALDO_DISPONIBLE = "No tiene saldo disponible para vincularse al fondo %s";
    private static final String MONTO_MENOR_AL_MINIMO = "El monto no alcanza el mínimo de vinculación al fondo %s";

    private final DaoCliente daoCliente;
    private final DaoFondo daoFondo;
    private final RepositorioTransaccion repositorioTransaccion;

    public TransaccionDTO ejecutar(Transaccion transaccion) {
        ClienteDTO cliente = daoCliente.obtenerPorId(transaccion.getClienteId());
        if (cliente == null) {
            throw new ExcepcionSinDatos(EL_CLIENTE_NO_EXISTE);
        }

        FondoDTO fondo = daoFondo.obtenerPorId(transaccion.getFondoId());
        if (fondo == null) {
            throw new ExcepcionSinDatos(FONDO_NO_ENCONTRADO);
        }

        if (transaccion.getMonto() < fondo.getMontoMinimo()) {
            throw new ExcepcionValorInvalido(String.format(MONTO_MENOR_AL_MINIMO, fondo.getNombre()));
        }

        if (cliente.getSaldo() < transaccion.getMonto()) {
            throw new ExcepcionValorInvalido(String.format(NO_TIENE_SALDO_DISPONIBLE, fondo.getNombre()));
        }

        Double nuevoSaldo = cliente.getSaldo() - transaccion.getMonto();
        repositorioTransaccion.crear(transaccion, transaccion.getClienteId(), nuevoSaldo);

        return new TransaccionDTO(
                transaccion.getId(),
                transaccion.getFondoId(),
                fondo.getNombre(),
                transaccion.getClienteId(),
                transaccion.getMonto(),
                transaccion.getEstado(),
                transaccion.getPreferenciaNotificacion(),
                transaccion.getFecha().toString()
        );
    }
}
