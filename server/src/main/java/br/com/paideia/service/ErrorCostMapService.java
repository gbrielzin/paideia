package br.com.paideia.service;

import br.com.paideia.domain.model.InepItem;
import br.com.paideia.domain.model.QuestionImpact;
import br.com.paideia.domain.model.TriScore;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ErrorCostMapService {

    private final TriEngine triEngine;

    public ErrorCostMapService(TriEngine triEngine) {
        this.triEngine = triEngine;
    }

    /**
     * Calcula o mapa de custo marginal em pontos para cada questão da prova.
     * Para questões acertadas, o custo é 0.0.
     * Para questões erradas, calcula a pontuação projetada caso a questão tivesse sido acertada.
     */
    public List<QuestionImpact> calculateCostMap(
            List<InepItem> items,
            Map<Integer, Boolean> responses,
            Map<Integer, String> studentAnswers,
            TriScore baselineScore
    ) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        List<QuestionImpact> impacts = new ArrayList<>(items.size());

        for (InepItem item : items) {
            int qNum = item.questionNumber();
            String chosen = studentAnswers != null ? studentAnswers.get(qNum) : null;
            Boolean isCorrect = responses != null ? responses.get(qNum) : Boolean.FALSE;
            boolean correct = Boolean.TRUE.equals(isCorrect);

            double pointsLost = 0.0;

            if (!correct) {
                // Simula o vetor de respostas com este item como correto
                Map<Integer, Boolean> simulatedResponses = new HashMap<>(responses != null ? responses : Collections.emptyMap());
                simulatedResponses.put(qNum, true);

                TriScore simulatedScore = triEngine.estimateScore(items, simulatedResponses);
                double delta = simulatedScore.estimatedScore() - baselineScore.estimatedScore();
                // O ganho marginal deve ser não-negativo e arredondado para 1 casa decimal
                pointsLost = Math.max(0.0, Math.round(delta * 10.0) / 10.0);
            }

            impacts.add(new QuestionImpact(
                    qNum,
                    chosen != null ? chosen.trim().toUpperCase() : "-",
                    item.correctAlternative(),
                    correct,
                    item.topic(),
                    item.difficultyLevel(),
                    pointsLost
            ));
        }

        return impacts;
    }
}
