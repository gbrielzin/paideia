package br.com.paideia.domain.enums;

import lombok.Getter;

@Getter
public enum KnowledgeArea {
    MATHEMATICS("Matemática e suas Tecnologias"),
    NATURAL_SCIENCES("Ciências da Natureza e suas Tecnologias"),
    HUMAN_SCIENCES("Ciências Humanas e suas Tecnologias"),
    LANGUAGES("Linguagens, Códigos e suas Tecnologias");

    private final String description;

    KnowledgeArea(String description) {
        this.description = description;
    }
}
