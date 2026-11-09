package br.com.paideia.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class AnalysisControllerTest {

	// Respostas ficticias de teste
	static final String ANSWERS = "ABCDEABCDEABCDEABCDEABCDEABCDEABCDEABCDEABCDE";

	@Autowired
	MockMvc mvc;

	private static String body(String answers) {
		return "{\"answers\":\"" + answers + "\"}";
	}

	@Test
	void returnsScoreErrorMapAndReviewList() throws Exception {
		mvc.perform(post("/api/analysis").contentType(MediaType.APPLICATION_JSON).content(body(ANSWERS)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.notice").value("Estimativa, não é a nota oficial."))
				.andExpect(jsonPath("$.score").isNumber())
				.andExpect(jsonPath("$.error").isNumber())
				.andExpect(jsonPath("$.exam.scoredItems").value(43))
				.andExpect(jsonPath("$.errorMap[0].cost").isNumber())
				.andExpect(jsonPath("$.reviewFirst.length()").value(5))
				.andExpect(jsonPath("$.reviewFirst[0].position").isNumber());
	}

	@Test
	void rejectsWrongInputWithFixedMessage() throws Exception {
		mvc.perform(post("/api/analysis").contentType(MediaType.APPLICATION_JSON).content(body("ABC")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value(ApiExceptionHandler.MESSAGE));
		mvc.perform(post("/api/analysis").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
		mvc.perform(post("/api/analysis").contentType(MediaType.APPLICATION_JSON).content("lixo"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void neverLogsTheAnswers(CapturedOutput output) throws Exception {
		String marker = "ABBBAEEEDDABBBAEEEDDABBBAEEEDDABBBAEEEDDABBBA";
		mvc.perform(post("/api/analysis").contentType(MediaType.APPLICATION_JSON).content(body(marker)))
				.andExpect(status().isOk());
		String invalid = "XYZXYZXYZXYZXYZXYZXYZXYZXYZXYZXYZXYZXYZXYZXYZ";
		mvc.perform(post("/api/analysis").contentType(MediaType.APPLICATION_JSON).content(body(invalid)))
				.andExpect(status().isBadRequest());
		assertThat(output.getAll()).doesNotContain(marker).doesNotContain(invalid);
	}
}
