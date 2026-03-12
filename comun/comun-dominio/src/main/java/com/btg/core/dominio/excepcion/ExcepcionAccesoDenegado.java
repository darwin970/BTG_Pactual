package com.btg.core.dominio.excepcion;

public class ExcepcionAccesoDenegado extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExcepcionAccesoDenegado(String mensaje) {
        super(mensaje);
    }
}
