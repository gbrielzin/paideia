package br.com.paideia.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Confere que o motor Java reproduz os casos gerados por data/tri_reference.py.
 * Tolerância: 1e-6 ponto na nota, no erro e no custo de cada erro. Os dois códigos usam a mesma fórmula
 * e a diferença esperada é de ordem 1e-12 (arredondamento de ponto flutuante, ordem das somas).
 */
class TriEngineTest {

	static final double TOLERANCE = 1e-6;

	static TriEngine engine;
	static JsonNode cases;

	@BeforeAll
	static void setUp() throws Exception {
		engine = new TriEngine(ExamLoader.load(ExamLoader.DEFAULT_EXAM));
		try (InputStream in = new ClassPathResource("test_cases_mt_2025_azul.json").getInputStream()) {
			cases = JsonMapper.builder().build().readTree(in).get("cases");
		}
	}

	@Test
	void reproducesEveryPythonCase() {
		assertTrue(cases.size() >= 20);
		double worst = 0;
		for (JsonNode c : cases) {
			String name = c.get("name").asString();
			JsonNode expected = c.get("expected");
			Analysis got = engine.analyze(c.get("answers").asString());

			worst = Math.max(worst, diff(name + " nota", expected.get("score").asDouble(), got.score()));
			worst = Math.max(worst, diff(name + " erro", expected.get("error").asDouble(), got.error()));

			JsonNode map = expected.get("errorMap");
			assertEquals(map.size(), got.errorMap().size(), name + ": tamanho do mapa de erros");
			for (int i = 0; i < map.size(); i++) {
				ErrorCost e = got.errorMap().get(i);
				assertEquals(map.get(i).get("position").asInt(), e.position(), name + ": ordem do erro " + i);
				worst = Math.max(worst, diff(name + " custo", map.get(i).get("cost").asDouble(), e.cost()));
				worst = Math.max(worst,
						diff(name + " nota se certo", map.get(i).get("scoreIfCorrect").asDouble(), e.scoreIfCorrect()));
			}
		}
		System.out.println("Maior diferença Java x Python: " + worst);
	}

	private static double diff(String what, double expected, double actual) {
		double d = Math.abs(expected - actual);
		assertTrue(d <= TOLERANCE, what + ": esperado " + expected + " obtido " + actual);
		return d;
	}

	@Test
	void errorMapIsSortedFromMostToLeastExpensive() {
		List<ErrorCost> map = engine.analyze(".".repeat(45)).errorMap();
		assertEquals(43, map.size());
		for (int i = 1; i < map.size(); i++) {
			assertTrue(map.get(i - 1).cost() >= map.get(i).cost());
		}
	}

	@Test
	void annulledItemsDoNotChangeTheScore() {
		Analysis a = engine.analyze(".".repeat(45));
		char[] other = ".".repeat(45).toCharArray();
		other[172 - 136] = 'C';
		other[174 - 136] = 'C';
		assertEquals(a.score(), engine.analyze(new String(other)).score(), TOLERANCE);
	}

	@Test
	void rejectsWrongLength() {
		assertThrows(IllegalArgumentException.class, () -> engine.analyze("ABC"));
		assertThrows(IllegalArgumentException.class, () -> engine.analyze(null));
	}
}
