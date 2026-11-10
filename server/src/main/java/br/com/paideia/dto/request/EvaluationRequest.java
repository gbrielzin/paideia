package br.com.paideia.dto.request;

import br.com.paideia.domain.enums.BookletColor;
import br.com.paideia.domain.enums.KnowledgeArea;
import br.com.paideia.exception.InvalidAnswerException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record EvaluationRequest(
        @NotNull(message = "O ano do exame é obrigatório.")
        Integer examYear,

        @NotNull(message = "A área do conhecimento é obrigatória.")
        KnowledgeArea knowledgeArea,

        @NotNull(message = "A cor do caderno é obrigatória.")
        BookletColor bookletColor,

        @NotNull(message = "A lista de respostas é obrigatória.")
        @Size(min = 1, message = "Ao menos uma resposta deve ser enviada para cálculo da avaliação.")
        List<@Valid StudentAnswerItem> answers
) {
    public Map<Integer, String> toAnswersMap() {
        if (answers == null) {
            return Map.of();
        }
        Map<Integer, String> map = new HashMap<>();
        for (StudentAnswerItem item : answers) {
            if (item.questionNumber() != null) {
                if (map.containsKey(item.questionNumber())) {
                    throw new InvalidAnswerException(
                            String.format("A questão %d foi enviada mais de uma vez na mesma requisição.", item.questionNumber())
                    );
                }
                map.put(item.questionNumber(), item.chosenAlternative());
            }
        }
        return map;
    }
}
