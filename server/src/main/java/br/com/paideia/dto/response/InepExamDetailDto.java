package br.com.paideia.dto.response;

import br.com.paideia.domain.enums.BookletColor;
import br.com.paideia.domain.enums.KnowledgeArea;
import br.com.paideia.domain.model.InepExam;

import java.util.List;

public record InepExamDetailDto(
        int year,
        KnowledgeArea knowledgeArea,
        String knowledgeAreaDescription,
        BookletColor bookletColor,
        String bookletColorDescription,
        int totalQuestions,
        List<InepItemSummaryDto> items
) {
    public static InepExamDetailDto fromDomain(InepExam exam) {
        List<InepItemSummaryDto> itemDtos = exam.items() != null
                ? exam.items().stream().map(InepItemSummaryDto::fromDomain).toList()
                : List.of();

        return new InepExamDetailDto(
                exam.year(),
                exam.knowledgeArea(),
                exam.knowledgeArea().getDescription(),
                exam.bookletColor(),
                exam.bookletColor().getDescription(),
                itemDtos.size(),
                itemDtos
        );
    }
}
