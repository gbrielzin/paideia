package br.com.paideia.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InepCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/exams deve retornar a lista de provas disponíveis no catálogo")
    void shouldListAllExams() throws Exception {
        mockMvc.perform(get("/api/v1/exams").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].year", notNullValue()))
                .andExpect(jsonPath("$[0].knowledgeArea", notNullValue()))
                .andExpect(jsonPath("$[0].bookletColor", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/exams/2024/MATHEMATICS/BLUE deve retornar detalhes e questões do caderno")
    void shouldGetExamDetails() throws Exception {
        mockMvc.perform(get("/api/v1/exams/2024/MATHEMATICS/BLUE").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year", is(2024)))
                .andExpect(jsonPath("$.knowledgeArea", is("MATHEMATICS")))
                .andExpect(jsonPath("$.bookletColor", is("BLUE")))
                .andExpect(jsonPath("$.totalQuestions", is(45)))
                .andExpect(jsonPath("$.items", hasSize(45)))
                .andExpect(jsonPath("$.items[0].questionNumber", is(136)));
    }

    @Test
    @DisplayName("GET /api/v1/exams/1999/MATHEMATICS/PINK deve retornar 404 Not Found")
    void shouldReturn404ForNonExistentExam() throws Exception {
        mockMvc.perform(get("/api/v1/exams/1999/MATHEMATICS/PINK").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }
}
