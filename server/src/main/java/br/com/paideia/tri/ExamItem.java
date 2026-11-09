package br.com.paideia.tri;

/** Uma questão da prova. a, b e c são nulos quando o INEP anulou ou abandonou o item. */
public record ExamItem(
		int position,
		int itemCode,
		String answerKey,
		int skill,
		boolean annulled,
		Double a,
		Double b,
		Double c) {
}
