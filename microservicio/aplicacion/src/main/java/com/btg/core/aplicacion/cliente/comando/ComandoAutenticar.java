package com.btg.core.aplicacion.cliente.comando;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ComandoAutenticar {

    private String email;
    private String contrasena;
}
