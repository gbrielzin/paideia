package br.com.paideia.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Libera só os endereços do front listados em paideia.cors.allowed-origins. */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

	private final String[] origins;

	public CorsConfig(@Value("${paideia.cors.allowed-origins}") String[] origins) {
		this.origins = origins;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**").allowedOrigins(origins).allowedMethods("POST", "OPTIONS");
	}
}
