package br.com.paideia.domain.model;

public record TriScore(
        double theta,
        double estimatedScore,
        double standardError,
        int totalQuestions,
        int correctCount
) {
}
