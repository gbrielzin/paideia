package br.com.paideia.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Erros de entrada viram uma mensagem fixa. Não repetimos nem registramos o que o aluno enviou. */
@RestControllerAdvice
public class ApiExceptionHandler {

	static final String MESSAGE = "Envie 45 respostas: letras A a E ou ponto para branco.";

	@ExceptionHandler({ MethodArgumentNotValidException.class, HttpMessageNotReadableException.class })
	ResponseEntity<Map<String, String>> invalidInput() {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", MESSAGE));
	}
}
