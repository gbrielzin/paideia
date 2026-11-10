package br.com.paideia.domain.model;

import br.com.paideia.domain.enums.BookletColor;
import br.com.paideia.domain.enums.KnowledgeArea;

import java.util.List;

public record ExamEvaluationResult(
        int year,
        KnowledgeArea knowledgeArea,
        BookletColor bookletColor,
        TriScore score,
        List<QuestionImpact> errorCostMap,
        List<TopicRecommendation> topRecommendations,
        String disclaimer
) {
    public static final String OFFICIAL_DISCLAIMER =
            "Estimativa estatística calculada via Teoria de Resposta ao Item (3PL/EAP) com base nos parâmetros do INEP. Acompanhada do Erro Padrão de Medida (EPM). Não substitui o resultado oficial do ENEM.";
}
