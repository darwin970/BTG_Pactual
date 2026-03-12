package com.btg.core.infraestructura.cliente.adaptador.seguridad;

import com.btg.core.dominio.cliente.modelo.entidad.DatosToken;
import com.btg.core.dominio.cliente.modelo.entidad.RespuestaAutenticacion;
import com.btg.core.dominio.cliente.puerto.repositorio.GeneradorToken;
import com.btg.core.dominio.excepcion.ExcepcionAutenticacion;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class GeneradorTokenJwt implements GeneradorToken {

    private final SecretKey clave;
    private final long expiracion;

    public GeneradorTokenJwt(@Value("${jwt.secret}") String secreto,
                              @Value("${jwt.expiracion}") long expiracion) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracion = expiracion;
    }

    @Override
    public RespuestaAutenticacion generar(String clienteId, String email, String rol) {
        Date ahora = new Date();
        Date fechaExpiracion = new Date(ahora.getTime() + expiracion);

        String token = Jwts.builder()
                .subject(clienteId)
                .claim("email", email)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(fechaExpiracion)
                .signWith(clave)
                .compact();

        return new RespuestaAutenticacion(token, fechaExpiracion.getTime());
    }

    @Override
    public DatosToken validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new DatosToken(
                    claims.getSubject(),
                    claims.get("email", String.class),
                    claims.get("rol", String.class)
            );
        } catch (ExpiredJwtException e) {
            throw new ExcepcionAutenticacion("Token expirado");
        } catch (JwtException e) {
            throw new ExcepcionAutenticacion("Token inválido");
        }
    }
}
