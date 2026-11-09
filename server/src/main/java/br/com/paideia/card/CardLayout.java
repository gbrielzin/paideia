package br.com.paideia.card;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import tools.jackson.databind.json.JsonMapper;

/**
 * Medidas do cartão-resposta em milímetros (origem no canto superior esquerdo da folha).
 * O mesmo arquivo é usado pelo gerador do PDF (data/make_answer_card.py).
 */
public record CardLayout(
		double pageWidthMm,
		double pageHeightMm,
		double markSizeMm,
		List<List<Double>> markCentersMm,
		int firstPosition,
		int questions,
		int rowsPerBlock,
		List<Double> blockOriginsXMm,
		double firstRowCenterYMm,
		double rowPitchMm,
		double firstBubbleOffsetXMm,
		double bubblePitchMm,
		double bubbleDiameterMm,
		String letters) {

	public static final String RESOURCE = "card/layout.json";

	public static CardLayout load() {
		try (InputStream in = new ClassPathResource(RESOURCE).getInputStream()) {
			return JsonMapper.builder()
					.disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
					.build().readValue(in, CardLayout.class);
		}
		catch (IOException e) {
			throw new UncheckedIOException("Não foi possível ler o layout do cartão", e);
		}
	}

	/** Centro da bolinha da letra (0 a 4) da questão de índice 0 a 44, em milímetros: {x, y}. */
	public double[] bubbleCenterMm(int question, int letter) {
		int block = question / rowsPerBlock;
		int row = question % rowsPerBlock;
		double x = blockOriginsXMm.get(block) + firstBubbleOffsetXMm + letter * bubblePitchMm;
		double y = firstRowCenterYMm + row * rowPitchMm;
		return new double[] { x, y };
	}

	public double[][] markCenters() {
		double[][] out = new double[markCentersMm.size()][];
		for (int i = 0; i < out.length; i++) {
			out[i] = new double[] { markCentersMm.get(i).get(0), markCentersMm.get(i).get(1) };
		}
		return out;
	}
}
