package br.com.paideia.card;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import br.com.paideia.card.SyntheticCard.Conditions;

/**
 * Mede a taxa de acerto do leitor em fotos SINTÉTICAS com respostas conhecidas.
 * Não roda no CI. Para rodar (a pasta server/ é o diretório atual):
 *   mvn test -Dtest=SyntheticBenchmarkTest -Dpaideia.benchmark=true -Dpaideia.n=40 -Dpaideia.out=../scratch/medicao.md
 * Opcional: -Dpaideia.dumpDir=pasta grava um JPEG de exemplo por condição; -Dpaideia.only="giro forte;extremo" roda só as condições cujo nome contém algum desses trechos.
 */
@EnabledIfSystemProperty(named = "paideia.benchmark", matches = "true")
class SyntheticBenchmarkTest {

	record Scenario(String name, java.util.function.Function<Random, Conditions> make) {
	}

	static List<Scenario> scenarios() {
		return List.of(
				new Scenario("limpo (sem defeito)", r -> Conditions.clean()),
				new Scenario("giro de até 12 graus", r -> new Conditions(sign(r) * (3 + 9 * r.nextDouble()), 0.01, 0, false, 0, 0, 0, 0.78)),
				new Scenario("giro forte, 25 a 35 graus", r -> new Conditions(sign(r) * (25 + 10 * r.nextDouble()), 0.01, 0, false, 0, 0, 0, 0.55)),
				new Scenario("inclinação forte (perspectiva)", r -> new Conditions(sign(r) * 4 * r.nextDouble(), 0.06, 0, false, 0, 0, 0, 0.78)),
				new Scenario("inclinação extrema", r -> new Conditions(sign(r) * 4 * r.nextDouble(), 0.12, 0, false, 0, 0, 0, 0.78)),
				new Scenario("luz desigual", r -> new Conditions(0, 0.01, 0.8, false, 0, 0, 0, 0.78)),
				new Scenario("sombra em cima da folha", r -> new Conditions(0, 0.01, 0.4, true, 0, 0, 0, 0.78)),
				new Scenario("desfoque médio (raio 2)", r -> new Conditions(0, 0.01, 0, false, 2, 0, 0, 0.78)),
				new Scenario("desfoque forte (raio 4)", r -> new Conditions(0, 0.01, 0, false, 4, 0, 0, 0.78)),
				new Scenario("desfoque extremo (raio 7)", r -> new Conditions(0, 0.01, 0, false, 7, 0, 0, 0.78)),
				new Scenario("ruído", r -> new Conditions(0, 0.01, 0, false, 0, 15, 0, 0.78)),
				new Scenario("ruído forte", r -> new Conditions(0, 0.01, 0, false, 0, 40, 0, 0.78)),
				new Scenario("caneta fraca (cinza 100 a 140)", r -> new Conditions(0, 0.01, 0, false, 0, 0, 100, 0.78)),
				new Scenario("caneta muito fraca (cinza 140 a 180)", r -> new Conditions(0, 0.01, 0, false, 0, 0, 140, 0.78)),
				new Scenario("folha pequena na foto (longe)", r -> new Conditions(0, 0.01, 0, false, 0, 0, 0, 0.45)),
				new Scenario("tudo junto (difícil)", r -> new Conditions(sign(r) * (3 + 7 * r.nextDouble()), 0.04, 0.6, true, 2, 10, 0, 0.75)),
				new Scenario("tudo junto, extremo", r -> new Conditions(sign(r) * (10 + 10 * r.nextDouble()), 0.08, 1.0, true, 4, 25, 100, 0.65)));
	}

	static double sign(Random r) {
		return r.nextBoolean() ? 1 : -1;
	}

	@Test
	void measure() throws Exception {
		int n = Integer.getInteger("paideia.n", 20);
		String dump = System.getProperty("paideia.dumpDir");
		CardLayout layout = CardLayout.load();
		CardReader reader = new CardReader(layout);
		StringBuilder md = new StringBuilder();
		md.append("| Condição | Cartões (N) | Recusados | Cartões 45/45 certos | Questões certas | Erros que o leitor mandou conferir |\n");
		md.append("|---|---|---|---|---|---|\n");
		int totQ = 0;
		int totQok = 0;
		int totCards = 0;
		int totRefused = 0;
		int totPerfect = 0;
		int totWrong = 0;
		int totWrongFlagged = 0;
		long start = System.currentTimeMillis();
		int sc = 0;
		String only = System.getProperty("paideia.only");
		for (Scenario s : scenarios()) {
			Random rng = new Random(1000L + sc++);
			if (only != null && java.util.Arrays.stream(only.split(";")).noneMatch(s.name()::contains)) {
				continue;
			}
			int refused = 0;
			int perfect = 0;
			int questions = 0;
			int correct = 0;
			int wrong = 0;
			int wrongFlagged = 0;
			for (int i = 0; i < n; i++) {
				String truth = CardReaderTest.randomTruth(rng);
				byte[] jpeg = SyntheticCard.photo(layout, truth, s.make().apply(rng), rng);
				if (dump != null && i == 0) {
					Files.createDirectories(Path.of(dump));
					Files.write(Path.of(dump, "condicao_" + sc + ".jpg"), jpeg);
					Files.writeString(Path.of(dump, "condicao_" + sc + ".txt"), truth);
				}
				CardReading r;
				try {
					r = reader.read(ImageDecoder.decode(jpeg));
				}
				catch (CardNotFoundException e) {
					refused++;
					continue;
				}
				int ok = 0;
				for (int q = 0; q < truth.length(); q++) {
					questions++;
					if (r.answers().charAt(q) == truth.charAt(q)) {
						ok++;
						correct++;
					}
					else {
						wrong++;
						if (r.review().contains(layout.firstPosition() + q)) {
							wrongFlagged++;
						}
					}
				}
				if (ok == truth.length()) {
					perfect++;
				}
			}
			md.append(String.format("| %s | %d | %d | %d de %d | %s | %s |%n", s.name(), n, refused, perfect, n,
					questions == 0 ? "n/a" : String.format("%.2f%% (%d de %d)", 100.0 * correct / questions, correct, questions),
					wrong == 0 ? "nenhum erro" : String.format("%d de %d", wrongFlagged, wrong)));
			totQ += questions;
			totQok += correct;
			totCards += n;
			totRefused += refused;
			totPerfect += perfect;
			totWrong += wrong;
			totWrongFlagged += wrongFlagged;
		}
		md.append(String.format("| **Total** | %d | %d | %d de %d | %.2f%% (%d de %d) | %d de %d |%n", totCards, totRefused,
				totPerfect, totCards, 100.0 * totQok / Math.max(1, totQ), totQok, totQ, totWrongFlagged, totWrong));
		md.append(String.format("%nTempo total da medição: %.0f s (gerar as fotos + ler).%n", (System.currentTimeMillis() - start) / 1000.0));
		System.out.println(md);
		String out = System.getProperty("paideia.out");
		if (out != null) {
			Files.writeString(Path.of(out), md.toString(), StandardCharsets.UTF_8);
		}
	}
}
