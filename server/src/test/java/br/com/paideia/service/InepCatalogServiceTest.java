package br.com.paideia.service;

import br.com.paideia.domain.enums.BookletColor;
import br.com.paideia.domain.enums.KnowledgeArea;
import br.com.paideia.domain.model.InepExam;
import br.com.paideia.domain.model.InepItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InepCatalogServiceTest {

    @Autowired
    private InepCatalogService inepCatalogService;

    @Test
    @DisplayName("Deve carregar o catálogo do INEP em memória com sucesso na inicialização")
    void shouldLoadInepCatalogOnStartup() {
        assertThat(inepCatalogService.countExams()).isGreaterThanOrEqualTo(2);
        assertThat(inepCatalogService.countTotalItems()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Deve recuperar o caderno de Matemática 2024 Azul com 45 itens e parâmetros calibrados")
    void shouldGetMath2024BlueExamWith45Items() {
        Optional<InepExam> examOpt = inepCatalogService.getExam(2024, KnowledgeArea.MATHEMATICS, BookletColor.BLUE);

        assertThat(examOpt).isPresent();
        InepExam exam = examOpt.get();
        assertThat(exam.year()).isEqualTo(2024);
        assertThat(exam.knowledgeArea()).isEqualTo(KnowledgeArea.MATHEMATICS);
        assertThat(exam.bookletColor()).isEqualTo(BookletColor.BLUE);
        assertThat(exam.items()).hasSize(45);

        InepItem firstItem = exam.items().getFirst();
        assertThat(firstItem.questionNumber()).isEqualTo(136);
        assertThat(firstItem.discriminationA()).isPositive();
        assertThat(firstItem.difficultyB()).isBetween(-4.0, 4.0);
        assertThat(firstItem.guessingC()).isBetween(0.0, 0.5);
        assertThat(firstItem.topic()).isNotBlank();
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar exame inexistente no catálogo")
    void shouldReturnEmptyForNonExistentExam() {
        Optional<InepExam> examOpt = inepCatalogService.getExam(1999, KnowledgeArea.MATHEMATICS, BookletColor.PINK);
        assertThat(examOpt).isEmpty();
    }

    @Test
    @DisplayName("Deve listar todos os exames disponíveis ordenados")
    void shouldListAllAvailableExams() {
        List<InepExam> exams = inepCatalogService.listAllExams();
        assertThat(exams).isNotEmpty();
        assertThat(exams).extracting(InepExam::year).contains(2024);
    }
}
