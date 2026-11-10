package br.com.paideia.dto.response;

import br.com.paideia.domain.model.QuestionImpact;

public record QuestionImpactDto(
        int questionNumber,
        String chosenAlternative,
        String correctAlternative,
        boolean correct,
        String topic,
        String difficulty,
        double pointsLost
) {
    public static QuestionImpactDto fromDomain(QuestionImpact domain) {
        return new QuestionImpactDto(
                domain.questionNumber(),
                domain.chosenAlternative(),
                domain.correctAlternative(),
                domain.correct(),
                domain.topic(),
                domain.difficultyLevel(),
                domain.pointsLost()
        );
    }
}
