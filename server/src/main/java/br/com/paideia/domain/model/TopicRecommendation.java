package br.com.paideia.domain.model;

import java.util.List;

public record TopicRecommendation(
        int rank,
        String topic,
        double potentialGain,
        int wrongQuestionsCount,
        String summary,
        List<Integer> questionNumbers
) {
}
