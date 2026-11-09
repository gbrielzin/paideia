package br.com.paideia.tri;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.springframework.core.io.ClassPathResource;

import tools.jackson.databind.json.JsonMapper;

/** Lê a prova (JSON público do INEP) de resources/exams. Sem banco. */
public final class ExamLoader {

	public static final String DEFAULT_EXAM = "exams/enem-2025-mt-azul.json";

	private ExamLoader() {
	}

	public static Exam load(String resourcePath) {
		try (InputStream in = new ClassPathResource(resourcePath).getInputStream()) {
			return JsonMapper.builder().build().readValue(in, Exam.class);
		}
		catch (IOException e) {
			throw new UncheckedIOException("Não foi possível ler a prova " + resourcePath, e);
		}
	}
}
