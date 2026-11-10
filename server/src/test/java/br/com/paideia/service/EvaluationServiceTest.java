package br.com.paideia.service;

import br.com.paideia.domain.enums.BookletColor;
import br.com.paideia.domain.enums.KnowledgeArea;
import br.com.paideia.domain.model.ExamEvaluationResult;
import br.com.paideia.domain.model.InepExam;
import br.com.paideia.domain.model.InepItem;
import br.com.paideia.domain.model.QuestionImpact;
import br.com.paideia.exception.ExamNotFoundException;
import br.com.paideia.exception.InvalidAnswerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class EvaluationServiceTest {

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private InepCatalogService inepCatalogService;

    private InepExam mathExam;

    @BeforeEach
    void setUp() {
        Optional<InepExam> examOpt = inepCatalogService.getExam(2024, KnowledgeArea.MATHEMATICS, BookletColor.BLUE);
        assertThat(examOpt).isPresent();
        this.mathExam = examOpt.get();
    }

    @Test
    @DisplayName("Deve avaliar simulado com sucesso, calculando nota, margem de erro, mapa de custo e top 3 tópicos")
    void shouldEvaluateExamSuccessfullyWithCalculatedScoreAndErrorCost() {
        Map<Integer, String> studentAnswers = new HashMap<>();
        // Aluno acerta 30 questões e erra 15
        int count = 0;
        for (InepItem item : mathExam.items()) {
            if (count < 30) {
                studentAnswers.put(item.questionNumber(), item.correctAlternative());
            } else {
                studentAnswers.put(item.questionNumber(), "X"); // Errada
            }
            count++;
        }

        ExamEvaluationResult result = evaluationService.evaluate(
                2024,
                KnowledgeArea.MATHEMATICS,
                BookletColor.BLUE,
                studentAnswers
        );

        assertThat(result.year()).isEqualTo(2024);
        assertThat(result.knowledgeArea()).isEqualTo(KnowledgeArea.MATHEMATICS);
        assertThat(result.bookletColor()).isEqualTo(BookletColor.BLUE);
        assertThat(result.score().totalQuestions()).isEqualTo(45);
        assertThat(result.score().correctCount()).isEqualTo(30);
        assertThat(result.score().estimatedScore()).isBetween(550.0, 750.0);
        assertThat(result.score().standardError()).isPositive();
        assertThat(result.disclaimer()).isNotBlank();

        // Mapa de custo
        assertThat(result.errorCostMap()).hasSize(45);
        long wrongItems = result.errorCostMap().stream().filter(i -> !i.correct()).count();
        assertThat(wrongItems).isEqualTo(15);

        // Top recomendações
        assertThat(result.topRecommendations()).isNotEmpty().hasSizeLessThanOrEqualTo(3);
        assertThat(result.topRecommendations().getFirst().rank()).isEqualTo(1);
        assertThat(result.topRecommendations().getFirst().potentialGain()).isPositive();
        assertThat(result.topRecommendations().getFirst().summary()).isNotBlank();
    }

    @Test
    @DisplayName("Deve lançar ExamNotFoundException para ano ou caderno inexistente")
    void shouldThrowExamNotFoundExceptionForInvalidExam() {
        Map<Integer, String> answers = Map.of(136, "A");
        assertThatThrownBy(() -> evaluationService.evaluate(1990, KnowledgeArea.MATHEMATICS, BookletColor.BLUE, answers))
                .isInstanceOf(ExamNotFoundException.class)
                .hasMessageContaining("Prova não encontrada");
    }

    @Test
    @DisplayName("Deve lançar InvalidAnswerException para questão fora do caderno selecionado")
    void shouldThrowInvalidAnswerExceptionForUnknownQuestionNumber() {
        Map<Integer, String> answers = Map.of(999, "A");
        assertThatThrownBy(() -> evaluationService.evaluate(2024, KnowledgeArea.MATHEMATICS, BookletColor.BLUE, answers))
                .isInstanceOf(InvalidAnswerException.class)
                .hasMessageContaining("não pertence à prova selecionada");
    }

    @Test
    @DisplayName("Deve retornar ganho potencial zerado e sem recomendações para prova 100% correta")
    void shouldHandleAllCorrectAnswersWithZeroPointsLost() {
        Map<Integer, String> studentAnswers = new HashMap<>();
        for (InepItem item : mathExam.items()) {
            studentAnswers.put(item.questionNumber(), item.correctAlternative());
        }

        ExamEvaluationResult result = evaluationService.evaluate(
                2024,
                KnowledgeArea.MATHEMATICS,
                BookletColor.BLUE,
                studentAnswers
        );

        assertThat(result.score().correctCount()).isEqualTo(45);
        assertThat(result.topRecommendations()).isEmpty();
        assertThat(result.errorCostMap()).allMatch(QuestionImpact::correct);
        assertThat(result.errorCostMap()).allMatch(i -> i.pointsLost() == 0.0);
    }
}
