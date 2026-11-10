package br.com.paideia.dto.response;

import br.com.paideia.domain.model.InepItem;

public record InepItemSummaryDto(
        int questionNumber,
        String topic,
        String skillCode,
        String skillDescription,
        String difficultyLevel
) {
    public static InepItemSummaryDto fromDomain(InepItem item) {
        return new InepItemSummaryDto(
                item.questionNumber(),
                item.topic(),
                item.skillCode(),
                item.skillDescription(),
                item.difficultyLevel()
        );
    }
}
