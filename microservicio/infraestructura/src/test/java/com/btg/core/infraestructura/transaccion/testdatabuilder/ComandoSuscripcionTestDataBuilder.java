package com.btg.core.infraestructura.transaccion.testdatabuilder;

import com.btg.core.aplicacion.transaccion.comando.ComandoSuscripcion;

public class ComandoSuscripcionTestDataBuilder {

    private String clienteId;
    private String fondoId;
    private Double monto;
    private String preferenciaNotificacion;

    public ComandoSuscripcionTestDataBuilder() {
        this.clienteId = "cliente-001";
        this.fondoId = "1";
        this.monto = 75000.0;
        this.preferenciaNotificacion = "EMAIL";
    }

    public ComandoSuscripcionTestDataBuilder conClienteId(String clienteId) {
        this.clienteId = clienteId;
        return this;
    }

    public ComandoSuscripcionTestDataBuilder conFondoId(String fondoId) {
        this.fondoId = fondoId;
        return this;
    }

    public ComandoSuscripcionTestDataBuilder conMonto(Double monto) {
        this.monto = monto;
        return this;
    }

    public ComandoSuscripcionTestDataBuilder conPreferenciaNotificacion(String preferenciaNotificacion) {
        this.preferenciaNotificacion = preferenciaNotificacion;
        return this;
    }

    public ComandoSuscripcion construir() {
        return new ComandoSuscripcion(clienteId, fondoId, monto, preferenciaNotificacion);
    }
}
