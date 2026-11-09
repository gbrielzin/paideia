package br.com.paideia.card;

import java.util.List;

/**
 * Resultado da leitura do cartão.
 *
 * @param answers 45 caracteres: letra A a E, ou "." para branco ou ambígua (a nota trata os dois como erro)
 * @param questions uma entrada por questão, com o estado e a confiança da leitura
 * @param review posições das questões que o aluno deve conferir
 */
public record CardReading(String answers, List<Question> questions, List<Integer> review) {

	public enum Status { MARKED, BLANK, AMBIGUOUS }

	/** confidence vai de 0 a 1. */
	public record Question(int position, String answer, Status status, double confidence) {
	}
}
