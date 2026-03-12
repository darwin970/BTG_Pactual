package com.btg.core.aplicacion;

public class ComandoRespuesta<T> {

    private T valor;
    private String advertencia;

    public ComandoRespuesta(T valor) {
        this.valor = valor;
    }

    public T getValor() {
        return valor;
    }

    public String getAdvertencia() {
        return advertencia;
    }

    public void setAdvertencia(String advertencia) {
        this.advertencia = advertencia;
    }
}
