package com.btg.core.aplicacion.transaccion.comando;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComandoSuscripcion {

    private String clienteId;
    private String fondoId;
    private Double monto;
    private String preferenciaNotificacion;
}
