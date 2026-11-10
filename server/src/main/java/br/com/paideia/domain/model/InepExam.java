package br.com.paideia.domain.model;

import br.com.paideia.domain.enums.BookletColor;
import br.com.paideia.domain.enums.KnowledgeArea;

import java.util.List;
import java.util.Optional;

public record InepExam(
        int year,
        KnowledgeArea knowledgeArea,
        BookletColor bookletColor,
        int totalQuestions,
        List<InepItem> items
) {
    public Optional<InepItem> findItemByNumber(int questionNumber) {
        if (items == null) {
            return Optional.empty();
        }
        return items.stream()
                .filter(item -> item.questionNumber() == questionNumber)
                .findFirst();
    }
}
