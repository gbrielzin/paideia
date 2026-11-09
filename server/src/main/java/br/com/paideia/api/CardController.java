package br.com.paideia.api;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.paideia.card.CardReader;
import br.com.paideia.card.CardReading;
import br.com.paideia.card.ImageDecoder;

/**
 * Lê o cartão-resposta de uma foto. Sem estado (decisão 0003): a foto fica só na memória durante a chamada,
 * não é gravada e nem a foto nem as respostas lidas entram em log.
 */
@RestController
@RequestMapping("/api")
public class CardController {

	private final CardReader reader;

	public CardController(CardReader reader) {
		this.reader = reader;
	}

	@PostMapping(value = "/read-card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public CardReading readCard(@RequestParam("photo") MultipartFile photo) throws IOException {
		return reader.read(ImageDecoder.decode(photo.getBytes()));
	}
}
