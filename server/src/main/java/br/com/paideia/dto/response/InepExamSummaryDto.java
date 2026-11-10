package br.com.paideia.dto.response;

import br.com.paideia.domain.enums.BookletColor;
import br.com.paideia.domain.enums.KnowledgeArea;
import br.com.paideia.domain.model.InepExam;

public record InepExamSummaryDto(
        int year,
        KnowledgeArea knowledgeArea,
        String knowledgeAreaDescription,
        BookletColor bookletColor,
        String bookletColorDescription,
        int totalQuestions
) {
    public static InepExamSummaryDto fromDomain(InepExam exam) {
        return new InepExamSummaryDto(
                exam.year(),
                exam.knowledgeArea(),
                exam.knowledgeArea().getDescription(),
                exam.bookletColor(),
                exam.bookletColor().getDescription(),
                exam.items() != null ? exam.items().size() : 0
        );
    }
}
