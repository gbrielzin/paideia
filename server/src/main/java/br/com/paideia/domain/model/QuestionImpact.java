package br.com.paideia.domain.model;

public record QuestionImpact(
        int questionNumber,
        String chosenAlternative,
        String correctAlternative,
        boolean correct,
        String topic,
        String difficultyLevel,
        double pointsLost
) {
}
