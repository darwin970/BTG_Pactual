package com.btg.core.infraestructura.configuracion;

import com.btg.core.dominio.cliente.puerto.dao.DaoCliente;
import com.btg.core.dominio.cliente.puerto.repositorio.EncriptadorContrasena;
import com.btg.core.dominio.cliente.puerto.repositorio.GeneradorToken;
import com.btg.core.dominio.cliente.puerto.repositorio.RepositorioCliente;
import com.btg.core.dominio.cliente.servicio.ServicioAutenticar;
import com.btg.core.dominio.cliente.servicio.ServicioConsultarCliente;
import com.btg.core.dominio.cliente.servicio.ServicioCrearCliente;
import com.btg.core.dominio.fondo.puerto.dao.DaoFondo;
import com.btg.core.dominio.transaccion.puerto.dao.DaoTransaccion;
import com.btg.core.dominio.transaccion.puerto.repositorio.RepositorioTransaccion;
import com.btg.core.dominio.transaccion.servicio.ServicioCancelarSuscripcion;
import com.btg.core.dominio.transaccion.servicio.ServicioConsultarTransacciones;
import com.btg.core.dominio.transaccion.servicio.ServicioSuscribirFondo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionBeanServicio {

    @Bean
    public ServicioCrearCliente servicioCrearCliente(RepositorioCliente repositorioCliente,
                                                     EncriptadorContrasena encriptadorContrasena) {
        return new ServicioCrearCliente(repositorioCliente, encriptadorContrasena);
    }

    @Bean
    public ServicioConsultarCliente servicioConsultarCliente(DaoCliente daoCliente) {
        return new ServicioConsultarCliente(daoCliente);
    }

    @Bean
    public ServicioAutenticar servicioAutenticar(RepositorioCliente repositorioCliente,
                                                  EncriptadorContrasena encriptadorContrasena,
                                                  GeneradorToken generadorToken) {
        return new ServicioAutenticar(repositorioCliente, encriptadorContrasena, generadorToken);
    }

    @Bean
    public ServicioSuscribirFondo servicioSuscribirFondo(DaoCliente daoCliente,
                                                          DaoFondo daoFondo,
                                                          RepositorioTransaccion repositorioTransaccion) {
        return new ServicioSuscribirFondo(daoCliente, daoFondo, repositorioTransaccion);
    }

    @Bean
    public ServicioCancelarSuscripcion servicioCancelarSuscripcion(DaoTransaccion daoTransaccion,
                                                                    DaoCliente daoCliente,
                                                                    DaoFondo daoFondo,
                                                                    RepositorioTransaccion repositorioTransaccion) {
        return new ServicioCancelarSuscripcion(daoTransaccion, daoCliente, daoFondo, repositorioTransaccion);
    }

    @Bean
    public ServicioConsultarTransacciones servicioConsultarTransacciones(DaoCliente daoCliente,
                                                                          DaoTransaccion daoTransaccion) {
        return new ServicioConsultarTransacciones(daoCliente, daoTransaccion);
    }
}
