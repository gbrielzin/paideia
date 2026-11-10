package br.com.paideia.dto.response;

import br.com.paideia.domain.enums.BookletColor;
import br.com.paideia.domain.enums.KnowledgeArea;
import br.com.paideia.domain.model.ExamEvaluationResult;

import java.util.List;

public record EvaluationResponse(
        int examYear,
        KnowledgeArea knowledgeArea,
        BookletColor bookletColor,
        double estimatedScore,
        double standardError,
        int totalQuestions,
        int correctCount,
        String disclaimer,
        List<TopicRecommendationDto> topRecommendations,
        List<QuestionImpactDto> errorCostMap
) {
    public static EvaluationResponse fromDomain(ExamEvaluationResult result) {
        List<TopicRecommendationDto> recDtos = result.topRecommendations() != null
                ? result.topRecommendations().stream().map(TopicRecommendationDto::fromDomain).toList()
                : List.of();

        List<QuestionImpactDto> impactDtos = result.errorCostMap() != null
                ? result.errorCostMap().stream().map(QuestionImpactDto::fromDomain).toList()
                : List.of();

        return new EvaluationResponse(
                result.year(),
                result.knowledgeArea(),
                result.bookletColor(),
                result.score().estimatedScore(),
                result.score().standardError(),
                result.score().totalQuestions(),
                result.score().correctCount(),
                result.disclaimer(),
                recDtos,
                impactDtos
        );
    }
}
