package com.btg.core.dominio.cliente.puerto.repositorio;

import com.btg.core.dominio.cliente.modelo.entidad.DatosToken;
import com.btg.core.dominio.cliente.modelo.entidad.RespuestaAutenticacion;

public interface GeneradorToken {

    RespuestaAutenticacion generar(String clienteId, String email, String rol);

    DatosToken validar(String token);
}
