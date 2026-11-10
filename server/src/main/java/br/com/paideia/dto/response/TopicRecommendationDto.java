package br.com.paideia.dto.response;

import br.com.paideia.domain.model.TopicRecommendation;

import java.util.List;

public record TopicRecommendationDto(
        int rank,
        String topic,
        double potentialGain,
        int wrongQuestionsCount,
        String summary,
        List<Integer> questionNumbers
) {
    public static TopicRecommendationDto fromDomain(TopicRecommendation domain) {
        return new TopicRecommendationDto(
                domain.rank(),
                domain.topic(),
                domain.potentialGain(),
                domain.wrongQuestionsCount(),
                domain.summary(),
                domain.questionNumbers()
        );
    }
}
