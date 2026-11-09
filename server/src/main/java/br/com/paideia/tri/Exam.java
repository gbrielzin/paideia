package br.com.paideia.tri;

import java.util.List;

/** Prova carregada do JSON em resources/exams. Os campos de fonte ficam para auditoria. */
public record Exam(
		String source,
		String sourceUrl,
		String retrievedOn,
		int edition,
		String area,
		String color,
		int examCode,
		List<ExamItem> items) {

	public int size() {
		return items.size();
	}
}
