package br.com.paideia.service;

import br.com.paideia.domain.model.QuestionImpact;
import br.com.paideia.domain.model.TopicRecommendation;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final int MAX_RECOMMENDATIONS = 3;

    /**
     * Agrupa as perdas por tópico e ranqueia as 3 áreas prioritárias de maior retorno em pontos.
     */
    public List<TopicRecommendation> generateTopRecommendations(List<QuestionImpact> impacts) {
        if (impacts == null || impacts.isEmpty()) {
            return Collections.emptyList();
        }

        // Filtra apenas questões com erro
        List<QuestionImpact> wrongImpacts = impacts.stream()
                .filter(i -> !i.correct())
                .toList();

        if (wrongImpacts.isEmpty()) {
            return Collections.emptyList();
        }

        // Agrupa por tópico
        Map<String, List<QuestionImpact>> groupedByTopic = wrongImpacts.stream()
                .collect(Collectors.groupingBy(QuestionImpact::topic));

        List<TopicAggregation> aggregations = new ArrayList<>();
        for (Map.Entry<String, List<QuestionImpact>> entry : groupedByTopic.entrySet()) {
            String topic = entry.getKey();
            List<QuestionImpact> topicImpacts = entry.getValue();

            double potentialGain = topicImpacts.stream()
                    .mapToDouble(QuestionImpact::pointsLost)
                    .sum();
            potentialGain = Math.round(potentialGain * 10.0) / 10.0;

            List<Integer> questionNumbers = topicImpacts.stream()
                    .map(QuestionImpact::questionNumber)
                    .sorted()
                    .toList();

            aggregations.add(new TopicAggregation(
                    topic,
                    potentialGain,
                    topicImpacts.size(),
                    questionNumbers
            ));
        }

        // Ordena por maior ganho potencial e depois por maior número de questões
        aggregations.sort(Comparator.comparingDouble(TopicAggregation::potentialGain).reversed()
                .thenComparingInt(TopicAggregation::wrongQuestionsCount).reversed());

        List<TopicRecommendation> recommendations = new ArrayList<>();
        int rank = 1;
        for (TopicAggregation agg : aggregations) {
            if (rank > MAX_RECOMMENDATIONS) {
                break;
            }

            String summary = String.format(
                    "Revisar %s gera retorno potencial de até %.1f pontos corrigindo %d %s prioritária(s).",
                    agg.topic(),
                    agg.potentialGain(),
                    agg.wrongQuestionsCount(),
                    agg.wrongQuestionsCount() == 1 ? "questão" : "questões"
            );

            recommendations.add(new TopicRecommendation(
                    rank,
                    agg.topic(),
                    agg.potentialGain(),
                    agg.wrongQuestionsCount(),
                    summary,
                    agg.questionNumbers()
            ));

            rank++;
        }

        return recommendations;
    }

    private record TopicAggregation(
            String topic,
            double potentialGain,
            int wrongQuestionsCount,
            List<Integer> questionNumbers
    ) {
    }
}
