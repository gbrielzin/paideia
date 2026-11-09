package br.com.paideia.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import br.com.paideia.card.CardNotFoundException;
import br.com.paideia.card.ImageDecoder;

/** Erros de entrada viram uma mensagem fixa. Não repetimos nem registramos o que o aluno enviou. */
@RestControllerAdvice
public class ApiExceptionHandler {

	static final String MESSAGE = "Envie 45 respostas: letras A a E ou ponto para branco.";
	static final String IMAGE_MESSAGE = "Envie uma foto JPEG ou PNG de até 8 MB.";
	static final String CARD_NOT_FOUND_MESSAGE =
			"Não encontrei os 4 quadrados pretos dos cantos. Fotografe a folha inteira, de frente e com boa luz.";

	@ExceptionHandler({ MethodArgumentNotValidException.class, HttpMessageNotReadableException.class })
	ResponseEntity<Map<String, String>> invalidInput() {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", MESSAGE));
	}

	@ExceptionHandler({ ImageDecoder.InvalidImageException.class, MaxUploadSizeExceededException.class,
			MissingServletRequestPartException.class, MissingServletRequestParameterException.class })
	ResponseEntity<Map<String, String>> invalidImage() {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", IMAGE_MESSAGE));
	}

	@ExceptionHandler(CardNotFoundException.class)
	ResponseEntity<Map<String, String>> cardNotFound() {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", CARD_NOT_FOUND_MESSAGE));
	}
}
