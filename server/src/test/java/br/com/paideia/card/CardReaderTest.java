package br.com.paideia.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.Random;

import org.junit.jupiter.api.Test;

import br.com.paideia.card.CardReading.Status;
import br.com.paideia.card.SyntheticCard.Conditions;

class CardReaderTest {

	static final CardLayout LAYOUT = CardLayout.load();
	static final CardReader READER = new CardReader(LAYOUT);

	/** Respostas fictícias sorteadas: cerca de 10% em branco. */
	static String randomTruth(Random rng) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < LAYOUT.questions(); i++) {
			sb.append(rng.nextDouble() < 0.10 ? '.' : LAYOUT.letters().charAt(rng.nextInt(5)));
		}
		return sb.toString();
	}

	private static CardReading readPhoto(String truth, Conditions c, long seed) {
		byte[] jpeg = SyntheticCard.photo(LAYOUT, truth, c, new Random(seed));
		return READER.read(ImageDecoder.decode(jpeg));
	}

	@Test
	void readsAFlatCleanCardExactly() {
		String truth = randomTruth(new Random(1));
		CardReading r = readPhoto(truth, Conditions.clean(), 11);
		assertEquals(truth, r.answers());
		assertEquals(45, r.questions().size());
		assertTrue(r.review().isEmpty(), "cartão limpo não deve pedir conferência: " + r.review());
	}

	@Test
	void readsATiltedCardWithUnevenLightBlurAndNoise() {
		String truth = randomTruth(new Random(2));
		CardReading r = readPhoto(truth, new Conditions(8, 0.03, 0.5, true, 1, 6, 0, 0.78), 12);
		assertEquals(truth, r.answers());
	}

	/** A medição achou um erro aqui: marcas giradas a 30 graus eram recusadas porque o filtro de "quadrado cheio" era rígido demais. */
	@Test
	void readsACardRotatedBy30Degrees() {
		String truth = randomTruth(new Random(3));
		CardReading r = readPhoto(truth, new Conditions(-30, 0.01, 0, false, 0, 0, 0, 0.55), 13);
		assertEquals(truth, r.answers());
	}

	@Test
	void refusesAPhotoWithoutTheFourMarks() {
		BufferedImage white = new BufferedImage(800, 1000, BufferedImage.TYPE_INT_RGB);
		var g = white.createGraphics();
		g.setColor(java.awt.Color.WHITE);
		g.fillRect(0, 0, 800, 1000);
		g.dispose();
		assertThrows(CardNotFoundException.class, () -> READER.read(white));
	}

	@Test
	void doubleMarkIsAmbiguousAndFlaggedForReview() {
		var q = CardReader.decide(140, "ABCDE", new double[] { 0.9, 0.8, 0.0, 0.0, 0.0 });
		assertEquals(Status.AMBIGUOUS, q.status());
		assertEquals(".", q.answer());
		assertTrue(q.confidence() < CardReader.REVIEW_BELOW);
	}

	@Test
	void lightMarkAndBlankDecisions() {
		assertEquals("C", CardReader.decide(140, "ABCDE", new double[] { 0, 0, 0.95, 0.02, 0 }).answer());
		assertEquals(Status.BLANK, CardReader.decide(140, "ABCDE", new double[] { 0.01, 0, 0.02, 0.02, 0 }).status());
		var weak = CardReader.decide(140, "ABCDE", new double[] { 0, 0, 0.40, 0, 0 });
		assertEquals("C", weak.answer());
		assertTrue(weak.confidence() < CardReader.REVIEW_BELOW);
	}

	@Test
	void homographyMapsTheCornersAndTheCenter() {
		double[][] src = { { 0, 0 }, { 10, 0 }, { 10, 20 }, { 0, 20 } };
		double[][] dst = { { 5, 5 }, { 105, 15 }, { 95, 125 }, { -5, 105 } };
		Homography h = Homography.from(src, dst);
		for (int i = 0; i < 4; i++) {
			double[] p = h.apply(src[i][0], src[i][1]);
			assertEquals(dst[i][0], p[0], 1e-9);
			assertEquals(dst[i][1], p[1], 1e-9);
		}
	}

	@Test
	void rejectsFilesThatAreNotJpegOrPng() {
		assertThrows(ImageDecoder.InvalidImageException.class, () -> ImageDecoder.decode("texto qualquer".getBytes()));
		assertThrows(ImageDecoder.InvalidImageException.class, () -> ImageDecoder.decode(new byte[0]));
		assertThrows(ImageDecoder.InvalidImageException.class,
				() -> ImageDecoder.decode(new byte[ImageDecoder.MAX_BYTES + 1]));
	}
}
