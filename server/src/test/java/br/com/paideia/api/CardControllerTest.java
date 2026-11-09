package br.com.paideia.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import br.com.paideia.card.CardLayout;
import br.com.paideia.card.SyntheticCard;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class CardControllerTest {

	@Autowired
	MockMvc mvc;

	@Value("${spring.servlet.multipart.file-size-threshold}")
	String threshold;

	@Value("${spring.servlet.multipart.max-file-size}")
	String maxFile;

	/** Foto sintética com respostas fictícias conhecidas (usa o mesmo gerador dos testes do leitor). */
	private static byte[] syntheticPhoto(String truth) {
		return SyntheticCard.photo(CardLayout.load(), truth, SyntheticCard.Conditions.clean(), new Random(5));
	}

	@Test
	void readsAPhotoAndReturnsAnswersAndConfidence() throws Exception {
		String truth = "ABCDE.ABCDE.ABCDE.ABCDE.ABCDE.ABCDE.ABCDE.ABCDE.A".substring(0, 45);
		var file = new MockMultipartFile("photo", "cartao.jpg", "image/jpeg", syntheticPhoto(truth));
		mvc.perform(multipart("/api/read-card").file(file))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.answers").value(truth))
				.andExpect(jsonPath("$.questions.length()").value(45))
				.andExpect(jsonPath("$.questions[0].position").value(136))
				.andExpect(jsonPath("$.questions[0].confidence").isNumber())
				.andExpect(jsonPath("$.review").isArray());
	}

	@Test
	void rejectsNonImageWithFixedMessage() throws Exception {
		var file = new MockMultipartFile("photo", "x.jpg", "image/jpeg", "isto não é uma imagem".getBytes());
		mvc.perform(multipart("/api/read-card").file(file))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value(ApiExceptionHandler.IMAGE_MESSAGE));
		mvc.perform(multipart("/api/read-card"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value(ApiExceptionHandler.IMAGE_MESSAGE));
	}

	@Test
	void photoWithoutTheFourMarksIsUnprocessable() throws Exception {
		BufferedImage white = new BufferedImage(800, 1000, BufferedImage.TYPE_INT_RGB);
		var g = white.createGraphics();
		g.setColor(java.awt.Color.WHITE);
		g.fillRect(0, 0, 800, 1000);
		g.dispose();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(white, "png", out);
		var file = new MockMultipartFile("photo", "x.png", "image/png", out.toByteArray());
		mvc.perform(multipart("/api/read-card").file(file))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.error").value(ApiExceptionHandler.CARD_NOT_FOUND_MESSAGE));
	}

	@Test
	void neverLogsThePhotoOrTheAnswersRead(CapturedOutput output) throws Exception {
		String truth = "EDCBAEDCBAEDCBAEDCBAEDCBAEDCBAEDCBAEDCBAEDCBA";
		var good = new MockMultipartFile("photo", "segredo-cartao.jpg", "image/jpeg", syntheticPhoto(truth));
		mvc.perform(multipart("/api/read-card").file(good)).andExpect(status().isOk());
		var bad = new MockMultipartFile("photo", "segredo-ruim.jpg", "image/jpeg", "lixo".getBytes());
		mvc.perform(multipart("/api/read-card").file(bad)).andExpect(status().isBadRequest());
		assertThat(output.getAll()).doesNotContain(truth).doesNotContain("segredo-cartao").doesNotContain("segredo-ruim");
	}

	@Test
	void uploadIsKeptInMemoryAndLimitedTo8Megabytes() {
		assertThat(maxFile).isEqualTo("8MB");
		// o limiar de gravação em arquivo temporário é maior que o limite do arquivo: nunca grava em disco
		assertThat(threshold).isEqualTo("9MB");
	}
}
