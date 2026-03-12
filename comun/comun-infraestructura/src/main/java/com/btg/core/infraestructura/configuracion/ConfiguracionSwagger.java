package com.btg.core.infraestructura.configuracion;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class ConfiguracionSwagger {
	@Bean
	public Docket apiDocket() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(getApiInfo())
				.consumes(Sets.newHashSet("application/json")).produces(Sets.newHashSet("application/json")).select()
				.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)).paths(PathSelectors.any())
				.build();
	}

	@Bean
	public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof WebMvcRequestHandlerProvider) {
					customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
				}
				return bean;
			}

			private void customizeSpringfoxHandlerMappings(List<RequestMappingInfoHandlerMapping> mappings) {
				List<RequestMappingInfoHandlerMapping> copy = mappings.stream()
						.filter(mapping -> mapping.getPatternParser() == null)
						.collect(Collectors.toList());
				mappings.clear();
				mappings.addAll(copy);
			}

			@SuppressWarnings("unchecked")
			private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
				try {
					Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
					field.setAccessible(true);
					return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
			}
		};
	}

	private static ApiInfo getApiInfo() {
		return new ApiInfoBuilder().title("Documentacion ApiRest").description("Descripcion de los servicios rest de la aplicacion")
				.version("1.0").contact(new Contact("Ceiba Software House", null, "contacto@ceiba.com.co")).build();
	}
}