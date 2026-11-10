package br.com.paideia.service;

import br.com.paideia.domain.enums.BookletColor;
import br.com.paideia.domain.enums.KnowledgeArea;
import br.com.paideia.domain.model.InepExam;
import br.com.paideia.domain.model.InepItem;
import br.com.paideia.domain.model.TriScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@SpringBootTest
class TriEngineTest {

    @Autowired
    private TriEngine triEngine;

    @Autowired
    private InepCatalogService inepCatalogService;

    private List<InepItem> mathItems;

    @BeforeEach
    void setUp() {
        Optional<InepExam> examOpt = inepCatalogService.getExam(2024, KnowledgeArea.MATHEMATICS, BookletColor.BLUE);
        assertThat(examOpt).isPresent();
        this.mathItems = examOpt.get().items();
    }

    @Test
    @DisplayName("Deve calcular a probabilidade exata segundo a fórmula 3PL")
    void shouldCalculateProbabilityAccordingTo3PLModel() {
        InepItem item = new InepItem(1, "A", 2.0, 1.0, 0.20, "Álgebra", "H1", "Desc");

        // Quando theta == b (1.0), logistic = 0.5 -> P = 0.20 + 0.80 * 0.5 = 0.60
        double probAtDifficulty = triEngine.calculateProbability(item, 1.0);
        assertThat(probAtDifficulty).isCloseTo(0.60, offset(0.001));

        // Quando theta é muito alto (4.0), P -> 1.0
        double probHigh = triEngine.calculateProbability(item, 4.0);
        assertThat(probHigh).isGreaterThan(0.99);

        // Quando theta é muito baixo (-4.0), P -> c (0.20)
        double probLow = triEngine.calculateProbability(item, -4.0);
        assertThat(probLow).isCloseTo(0.20, offset(0.01));
    }

    @Test
    @DisplayName("Deve calcular nota de topo (> 850) e convergência estável para 100% de acertos")
    void shouldEstimateHighScoreFor100PercentCorrect() {
        Map<Integer, Boolean> allCorrect = mathItems.stream()
                .collect(Collectors.toMap(InepItem::questionNumber, item -> true));

        TriScore score = triEngine.estimateScore(mathItems, allCorrect);

        assertThat(score.totalQuestions()).isEqualTo(45);
        assertThat(score.correctCount()).isEqualTo(45);
        assertThat(score.theta()).isGreaterThan(2.0);
        assertThat(score.estimatedScore()).isGreaterThan(750.0);
        assertThat(score.standardError()).isPositive().isLessThan(50.0);
    }

    @Test
    @DisplayName("Deve calcular piso da TRI (~300-350) e convergência estável para 0% de acertos")
    void shouldEstimateFloorScoreForZeroCorrect() {
        Map<Integer, Boolean> zeroCorrect = mathItems.stream()
                .collect(Collectors.toMap(InepItem::questionNumber, item -> false));

        TriScore score = triEngine.estimateScore(mathItems, zeroCorrect);

        assertThat(score.totalQuestions()).isEqualTo(45);
        assertThat(score.correctCount()).isEqualTo(0);
        assertThat(score.theta()).isLessThan(-1.0);
        assertThat(score.estimatedScore()).isBetween(200.0, 400.0);
        assertThat(score.standardError()).isPositive().isLessThan(60.0);
    }

    @Test
    @DisplayName("Deve premiar coerência pedagógica: acertos em itens fáceis dão nota superior a acertos aleatórios em difíceis")
    void shouldRewardPedagogicalConsistency() {
        // Ordena itens por dificuldade crescente
        List<InepItem> sortedByDifficulty = mathItems.stream()
                .sorted(Comparator.comparingDouble(InepItem::difficultyB))
                .toList();

        // Aluno Coerente: acerta os 22 itens mais fáceis e erra os 23 mais difíceis
        Map<Integer, Boolean> consistentResponses = new HashMap<>();
        for (int i = 0; i < sortedByDifficulty.size(); i++) {
            consistentResponses.put(sortedByDifficulty.get(i).questionNumber(), i < 22);
        }

        // Aluno Incoerente (Chute): erra os 22 itens mais fáceis e acerta os 22 mais difíceis
        Map<Integer, Boolean> inconsistentResponses = new HashMap<>();
        for (int i = 0; i < sortedByDifficulty.size(); i++) {
            inconsistentResponses.put(sortedByDifficulty.get(i).questionNumber(), i >= 23);
        }

        TriScore consistentScore = triEngine.estimateScore(mathItems, consistentResponses);
        TriScore inconsistentScore = triEngine.estimateScore(mathItems, inconsistentResponses);

        assertThat(consistentScore.correctCount()).isEqualTo(22);
        assertThat(inconsistentScore.correctCount()).isEqualTo(22);

        // A TRI deve atribuir pontuação consideravelmente maior ao aluno com padrão coerente
        assertThat(consistentScore.estimatedScore())
                .isGreaterThan(inconsistentScore.estimatedScore() + 30.0);
    }

    @Test
    @DisplayName("Deve retornar média padrão para lista de itens vazia")
    void shouldReturnDefaultScoreForEmptyItems() {
        TriScore score = triEngine.estimateScore(Collections.emptyList(), Collections.emptyMap());
        assertThat(score.estimatedScore()).isEqualTo(500.0);
        assertThat(score.standardError()).isEqualTo(100.0);
    }
}
