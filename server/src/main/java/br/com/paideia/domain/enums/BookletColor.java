package br.com.paideia.domain.enums;

import lombok.Getter;

@Getter
public enum BookletColor {
    BLUE("Azul"),
    YELLOW("Amarelo"),
    WHITE("Branco"),
    PINK("Rosa"),
    GRAY("Cinza");

    private final String description;

    BookletColor(String description) {
        this.description = description;
    }
}
