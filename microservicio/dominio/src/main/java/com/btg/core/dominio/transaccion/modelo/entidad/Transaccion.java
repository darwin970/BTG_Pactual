package com.btg.core.dominio.transaccion.modelo.entidad;

import com.btg.core.dominio.ValidadorArgumento;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Transaccion {

    private final String id;
    private final String fondoId;
    private final String clienteId;
    private final Double monto;
    private final String estado;
    private final String preferenciaNotificacion;
    private final LocalDateTime fecha;

    public Transaccion(String id, String fondoId, String clienteId, Double monto,
                       String estado, String preferenciaNotificacion, LocalDateTime fecha) {
        ValidadorArgumento.validarObligatorio(id, "El id de la transacción es obligatorio");
        ValidadorArgumento.validarObligatorio(fondoId, "El id del fondo es obligatorio");
        ValidadorArgumento.validarObligatorio(clienteId, "El id del cliente es obligatorio");
        ValidadorArgumento.validarObligatorio(monto, "El monto de la transacción es obligatorio");
        ValidadorArgumento.validarPositivo(monto, "El monto de la transacción debe ser positivo");
        ValidadorArgumento.validarObligatorio(estado, "El estado de la transacción es obligatorio");
        ValidadorArgumento.validarObligatorio(preferenciaNotificacion, "La preferencia de notificación es obligatoria");
        ValidadorArgumento.validarValido(preferenciaNotificacion, PreferenciaNotificacion.class,
                "La preferencia de notificación debe ser EMAIL o SMS");
        ValidadorArgumento.validarObligatorio(fecha, "La fecha de la transacción es obligatoria");

        this.id = id;
        this.fondoId = fondoId;
        this.clienteId = clienteId;
        this.monto = monto;
        this.estado = estado;
        this.preferenciaNotificacion = preferenciaNotificacion;
        this.fecha = fecha;
    }
}
