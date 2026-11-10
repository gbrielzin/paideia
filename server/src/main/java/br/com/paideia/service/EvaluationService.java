package br.com.paideia.service;

import br.com.paideia.domain.enums.BookletColor;
import br.com.paideia.domain.enums.KnowledgeArea;
import br.com.paideia.domain.model.*;
import br.com.paideia.exception.ExamNotFoundException;
import br.com.paideia.exception.InvalidAnswerException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EvaluationService {

    private final InepCatalogService inepCatalogService;
    private final TriEngine triEngine;
    private final ErrorCostMapService errorCostMapService;
    private final RecommendationService recommendationService;

    public EvaluationService(
            InepCatalogService inepCatalogService,
            TriEngine triEngine,
            ErrorCostMapService errorCostMapService,
            RecommendationService recommendationService
    ) {
        this.inepCatalogService = inepCatalogService;
        this.triEngine = triEngine;
        this.errorCostMapService = errorCostMapService;
        this.recommendationService = recommendationService;
    }

    public ExamEvaluationResult evaluate(
            int year,
            KnowledgeArea knowledgeArea,
            BookletColor bookletColor,
            Map<Integer, String> studentAnswers
    ) {
        InepExam exam = inepCatalogService.getExam(year, knowledgeArea, bookletColor)
                .orElseThrow(() -> new ExamNotFoundException(
                        String.format("Prova não encontrada no catálogo para o ano %d, área %s e caderno %s.",
                                year, knowledgeArea, bookletColor)
                ));

        Map<Integer, String> normalizedAnswers = studentAnswers != null ? studentAnswers : Collections.emptyMap();
        validateSubmittedQuestions(exam, normalizedAnswers);

        // Mapeia correção de cada item (acerto ou erro)
        Map<Integer, Boolean> isCorrectMap = new HashMap<>();
        for (InepItem item : exam.items()) {
            String chosen = normalizedAnswers.get(item.questionNumber());
            boolean isCorrect = item.isCorrect(chosen);
            isCorrectMap.put(item.questionNumber(), isCorrect);
        }

        // 1. Estimação da proficiência e nota TRI base com EPM
        TriScore score = triEngine.estimateScore(exam.items(), isCorrectMap);

        // 2. Mapa de custo marginal de erros
        List<QuestionImpact> errorCostMap = errorCostMapService.calculateCostMap(
                exam.items(),
                isCorrectMap,
                normalizedAnswers,
                score
        );

        // 3. Recomendação dos 3 tópicos prioritários
        List<TopicRecommendation> topRecommendations = recommendationService.generateTopRecommendations(errorCostMap);

        return new ExamEvaluationResult(
                year,
                knowledgeArea,
                bookletColor,
                score,
                errorCostMap,
                topRecommendations,
                ExamEvaluationResult.OFFICIAL_DISCLAIMER
        );
    }

    private void validateSubmittedQuestions(InepExam exam, Map<Integer, String> studentAnswers) {
        Set<Integer> validQuestionNumbers = new HashSet<>();
        for (InepItem item : exam.items()) {
            validQuestionNumbers.add(item.questionNumber());
        }

        for (Integer submittedQNum : studentAnswers.keySet()) {
            if (!validQuestionNumbers.contains(submittedQNum)) {
                throw new InvalidAnswerException(
                        String.format("A questão número %d não pertence à prova selecionada (%d - %s).",
                                submittedQNum, exam.year(), exam.knowledgeArea())
                );
            }
        }
    }
}
