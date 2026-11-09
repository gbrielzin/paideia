package br.com.paideia.api;

import java.util.List;

import br.com.paideia.tri.ErrorCost;

/**
 * Resposta da API. Tudo é estimativa: o campo notice repete isso para quem consumir a API.
 *
 * @param score nota estimada
 * @param error erro da estimativa (desvio padrão), em pontos
 * @param errorMap cada erro com a nota se estivesse certo, do mais caro ao mais barato
 * @param reviewFirst os erros mais caros, para revisar primeiro
 */
public record AnalysisResponse(
		String notice,
		ExamInfo exam,
		double score,
		double error,
		List<ErrorCost> errorMap,
		List<ErrorCost> reviewFirst) {

	/** Qual prova foi usada. scoredItems conta só os itens que entram na nota. */
	public record ExamInfo(int edition, String area, String color, int scoredItems, String source) {
	}
}
