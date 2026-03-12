package com.btg.core.infraestructura.configuracion;

import com.btg.core.dominio.cliente.modelo.entidad.DatosToken;
import com.btg.core.dominio.cliente.puerto.repositorio.GeneradorToken;
import com.btg.core.dominio.excepcion.ExcepcionAutenticacion;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class FiltroAutenticacionJwt extends OncePerRequestFilter {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String PREFIJO_BEARER = "Bearer ";

    private final GeneradorToken generadorToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HEADER_AUTHORIZATION);

        if (header != null && header.startsWith(PREFIJO_BEARER)) {
            String token = header.substring(PREFIJO_BEARER.length());
            try {
                DatosToken datos = generadorToken.validar(token);
                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(
                                datos.getClienteId(), null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_" + datos.getRol())
                                )
                        );
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            } catch (ExcepcionAutenticacion e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"nombreExcepcion\":\"ExcepcionAutenticacion\",\"mensaje\":\"" + e.getMessage() + "\"}"
                );
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
