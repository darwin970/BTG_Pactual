package com.btg.core.dominio.transaccion.modelo.entidad;

import java.time.LocalDateTime;

public class TransaccionTestDataBuilder {

    private String id;
    private String fondoId;
    private String clienteId;
    private Double monto;
    private String estado;
    private String preferenciaNotificacion;
    private LocalDateTime fecha;

    public TransaccionTestDataBuilder() {
        this.id = "txn-001";
        this.fondoId = "1";
        this.clienteId = "cliente-001";
        this.monto = 75000.0;
        this.estado = "ACTIVA";
        this.preferenciaNotificacion = "EMAIL";
        this.fecha = LocalDateTime.of(2026, 3, 12, 10, 0, 0);
    }

    public TransaccionTestDataBuilder conId(String id) {
        this.id = id;
        return this;
    }

    public TransaccionTestDataBuilder conFondoId(String fondoId) {
        this.fondoId = fondoId;
        return this;
    }

    public TransaccionTestDataBuilder conClienteId(String clienteId) {
        this.clienteId = clienteId;
        return this;
    }

    public TransaccionTestDataBuilder conMonto(Double monto) {
        this.monto = monto;
        return this;
    }

    public TransaccionTestDataBuilder conEstado(String estado) {
        this.estado = estado;
        return this;
    }

    public TransaccionTestDataBuilder conPreferenciaNotificacion(String preferenciaNotificacion) {
        this.preferenciaNotificacion = preferenciaNotificacion;
        return this;
    }

    public TransaccionTestDataBuilder conFecha(LocalDateTime fecha) {
        this.fecha = fecha;
        return this;
    }

    public Transaccion construir() {
        return new Transaccion(id, fondoId, clienteId, monto, estado, preferenciaNotificacion, fecha);
    }
}
