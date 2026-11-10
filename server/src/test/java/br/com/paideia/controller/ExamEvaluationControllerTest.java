package br.com.paideia.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExamEvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/v1/evaluations/calculate deve processar o simulado com sucesso e retornar nota, EPM e mapa de erros")
    void shouldCalculateEvaluationSuccessfully() throws Exception {
        String payload = """
                {
                  "examYear": 2024,
                  "knowledgeArea": "MATHEMATICS",
                  "bookletColor": "BLUE",
                  "answers": [
                    { "questionNumber": 136, "chosenAlternative": "B" },
                    { "questionNumber": 137, "chosenAlternative": "D" },
                    { "questionNumber": 138, "chosenAlternative": "C" },
                    { "questionNumber": 139, "chosenAlternative": "C" }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/evaluations/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.examYear", is(2024)))
                .andExpect(jsonPath("$.knowledgeArea", is("MATHEMATICS")))
                .andExpect(jsonPath("$.bookletColor", is("BLUE")))
                .andExpect(jsonPath("$.totalQuestions", is(45)))
                .andExpect(jsonPath("$.estimatedScore", greaterThan(200.0)))
                .andExpect(jsonPath("$.standardError", greaterThan(0.0)))
                .andExpect(jsonPath("$.disclaimer", containsString("Teoria de Resposta ao Item")))
                .andExpect(jsonPath("$.errorCostMap", hasSize(45)))
                .andExpect(jsonPath("$.topRecommendations", hasSize(lessThanOrEqualTo(3))));
    }

    @Test
    @DisplayName("POST /api/v1/evaluations/calculate com alternativa inválida deve retornar 400 Bad Request")
    void shouldReturn400ForInvalidAlternative() throws Exception {
        String payload = """
                {
                  "examYear": 2024,
                  "knowledgeArea": "MATHEMATICS",
                  "bookletColor": "BLUE",
                  "answers": [
                    { "questionNumber": 136, "chosenAlternative": "Z" }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/evaluations/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", hasItem(containsString("A, B, C, D ou E"))));
    }

    @Test
    @DisplayName("POST /api/v1/evaluations/calculate com questão duplicada deve retornar 400 Bad Request")
    void shouldReturn400ForDuplicateQuestion() throws Exception {
        String payload = """
                {
                  "examYear": 2024,
                  "knowledgeArea": "MATHEMATICS",
                  "bookletColor": "BLUE",
                  "answers": [
                    { "questionNumber": 136, "chosenAlternative": "A" },
                    { "questionNumber": 136, "chosenAlternative": "B" }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/evaluations/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("mais de uma vez")));
    }

    @Test
    @DisplayName("POST /api/v1/evaluations/calculate para exame inexistente deve retornar 404 Not Found")
    void shouldReturn404ForNonExistentExam() throws Exception {
        String payload = """
                {
                  "examYear": 1995,
                  "knowledgeArea": "MATHEMATICS",
                  "bookletColor": "PINK",
                  "answers": [
                    { "questionNumber": 136, "chosenAlternative": "A" }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/evaluations/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("não encontrada")));
    }

    @Test
    @DisplayName("POST /api/v1/evaluations/calculate com lista de respostas vazia deve retornar 400 Bad Request")
    void shouldReturn400ForEmptyAnswersList() throws Exception {
        String payload = """
                {
                  "examYear": 2024,
                  "knowledgeArea": "MATHEMATICS",
                  "bookletColor": "BLUE",
                  "answers": []
                }
                """;

        mockMvc.perform(post("/api/v1/evaluations/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }
}
