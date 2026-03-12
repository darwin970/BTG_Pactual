package com.btg.core.infraestructura.configuracion;

import com.btg.core.dominio.cliente.puerto.dao.DaoCliente;
import com.btg.core.dominio.cliente.puerto.repositorio.EncriptadorContrasena;
import com.btg.core.dominio.cliente.puerto.repositorio.GeneradorToken;
import com.btg.core.dominio.cliente.puerto.repositorio.RepositorioCliente;
import com.btg.core.dominio.cliente.servicio.ServicioAutenticar;
import com.btg.core.dominio.cliente.servicio.ServicioConsultarCliente;
import com.btg.core.dominio.cliente.servicio.ServicioCrearCliente;
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
}
