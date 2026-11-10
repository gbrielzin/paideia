package br.com.paideia.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record StudentAnswerItem(
        @NotNull(message = "O número da questão é obrigatório.")
        @Min(value = 1, message = "O número da questão deve ser positivo.")
        Integer questionNumber,

        @NotBlank(message = "A alternativa escolhida é obrigatória.")
        @Pattern(regexp = "^[A-Ea-e]$", message = "A alternativa escolhida deve ser A, B, C, D ou E.")
        String chosenAlternative
) {
}
