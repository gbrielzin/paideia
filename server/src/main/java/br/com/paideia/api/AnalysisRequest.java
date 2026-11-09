package br.com.paideia.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** Respostas marcadas: 45 caracteres, uma letra A a E por questão ou "." para branco. */
public record AnalysisRequest(
		@NotNull @Pattern(regexp = "[A-Ea-e.]{45}", message = "Envie 45 respostas: letras A a E ou ponto para branco") String answers) {
}
