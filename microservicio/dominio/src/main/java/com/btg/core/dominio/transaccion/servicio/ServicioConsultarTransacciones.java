package com.btg.core.dominio.transaccion.servicio;

import com.btg.core.dominio.cliente.puerto.dao.DaoCliente;
import com.btg.core.dominio.excepcion.ExcepcionSinDatos;
import com.btg.core.dominio.transaccion.modelo.dto.TransaccionDTO;
import com.btg.core.dominio.transaccion.puerto.dao.DaoTransaccion;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ServicioConsultarTransacciones {

    private static final String EL_CLIENTE_NO_EXISTE = "El cliente no existe";
    private static final String FECHA_CANCELACION_NA = "N/A";

    private final DaoCliente daoCliente;
    private final DaoTransaccion daoTransaccion;

    public List<TransaccionDTO> ejecutar(String clienteId) {
        if (daoCliente.obtenerPorId(clienteId) == null) {
            throw new ExcepcionSinDatos(EL_CLIENTE_NO_EXISTE);
        }

        return daoTransaccion.obtenerPorClienteId(clienteId).stream()
                .sorted(Comparator.comparing(TransaccionDTO::getFecha).reversed())
                .map(t -> new TransaccionDTO(
                        t.getId(), t.getFondoId(), t.getNombreFondo(), t.getClienteId(),
                        t.getMonto(), t.getEstado(), t.getPreferenciaNotificacion(),
                        t.getFecha(),
                        t.getFechaCancelacion() != null ? t.getFechaCancelacion() : FECHA_CANCELACION_NA))
                .collect(Collectors.toList());
    }
}
