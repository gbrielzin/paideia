package br.com.paideia.tri;

import java.util.List;

/** Resultado do cálculo: nota estimada, erro da estimativa e mapa de erros (do mais caro ao mais barato). */
public record Analysis(double score, double error, List<ErrorCost> errorMap) {
}
