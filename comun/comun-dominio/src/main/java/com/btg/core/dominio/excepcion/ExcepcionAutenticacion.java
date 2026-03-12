package com.btg.core.dominio.excepcion;

public class ExcepcionAutenticacion extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExcepcionAutenticacion(String mensaje) {
        super(mensaje);
    }
}
