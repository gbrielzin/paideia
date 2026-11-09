package br.com.paideia.card;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import br.com.paideia.card.CardReading.Question;
import br.com.paideia.card.CardReading.Status;

/**
 * Lê o cartão-resposta a partir de uma foto, só com a biblioteca padrão do Java. Sem estado e sem disco: a imagem
 * existe apenas na memória durante a chamada.
 *
 * Passos: (1) cinza e limiar local, que tolera luz desigual; (2) achar os quadrados pretos dos cantos;
 * (3) endireitar a folha pela perspectiva dos 4 cantos; (4) medir quanto cada bolinha está escura;
 * (5) decidir a letra de cada questão e a confiança.
 */
public class CardReader {

	static final int MAX_SIDE = 1800;
	static final double DARK_RATIO = 0.80;
	static final double SAMPLE_RADIUS_MM = 2.2;
	static final double SAMPLE_STEP_MM = 0.4;

	/** A partir deste preenchimento a bolinha conta como marcada. */
	static final double MARK_MIN = 0.35;
	/** Abaixo deste preenchimento a bolinha conta como vazia. */
	static final double BLANK_MAX = 0.15;
	/** Confiança abaixo disto manda o aluno conferir a questão. */
	static final double REVIEW_BELOW = 0.5;

	private static final double EXPECTED_RATIO = 180.0 / 267.0;
	private static final int TOP_CANDIDATES = 12;
	private static final double MAX_QUAD_SCORE = 0.8;

	private final CardLayout layout;

	public CardReader(CardLayout layout) {
		this.layout = layout;
	}

	public CardReading read(BufferedImage source) {
		BufferedImage img = scaleDown(source);
		int w = img.getWidth();
		int h = img.getHeight();
		int[] gray = toGray(img, w, h);
		boolean[] dark = localThreshold(gray, w, h);

		double[][] marks = findMarks(dark, w, h);
		double[][] cardMarks = layout.markCenters();
		Homography toImage = Homography.from(cardMarks, marks);

		List<Question> questions = new ArrayList<>();
		List<Integer> review = new ArrayList<>();
		StringBuilder answers = new StringBuilder();
		String letters = layout.letters();
		for (int q = 0; q < layout.questions(); q++) {
			double[] fill = new double[letters.length()];
			for (int l = 0; l < fill.length; l++) {
				fill[l] = darkFraction(dark, w, h, toImage, layout.bubbleCenterMm(q, l));
			}
			Question question = decide(layout.firstPosition() + q, letters, fill);
			questions.add(question);
			answers.append(question.answer());
			if (question.confidence() < REVIEW_BELOW) {
				review.add(question.position());
			}
		}
		return new CardReading(answers.toString(), List.copyOf(questions), List.copyOf(review));
	}

	/** Decide a letra de uma questão a partir do preenchimento das 5 bolinhas. */
	static Question decide(int position, String letters, double[] fill) {
		int best = 0;
		for (int i = 1; i < fill.length; i++) {
			if (fill[i] > fill[best]) {
				best = i;
			}
		}
		double max = fill[best];
		double second = 0;
		for (int i = 0; i < fill.length; i++) {
			if (i != best) {
				second = Math.max(second, fill[i]);
			}
		}
		if (max < BLANK_MAX) {
			double conf = clamp(1.0 - (max - 0.05) / 0.10);
			return new Question(position, ".", Status.BLANK, conf);
		}
		if (max < MARK_MIN || second >= MARK_MIN) {
			return new Question(position, ".", Status.AMBIGUOUS, 0.0);
		}
		double conf = Math.min(clamp((max - MARK_MIN) / 0.35), 1.0 - second / max);
		return new Question(position, String.valueOf(letters.charAt(best)), Status.MARKED, conf);
	}

	private static double clamp(double v) {
		return Math.max(0.0, Math.min(1.0, v));
	}

	// ---------- imagem ----------

	private static BufferedImage scaleDown(BufferedImage src) {
		int w = src.getWidth();
		int h = src.getHeight();
		int longest = Math.max(w, h);
		if (longest <= MAX_SIDE) {
			return src;
		}
		double s = (double) MAX_SIDE / longest;
		int nw = Math.max(1, (int) Math.round(w * s));
		int nh = Math.max(1, (int) Math.round(h * s));
		BufferedImage out = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = out.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(src, 0, 0, nw, nh, null);
		g.dispose();
		return out;
	}

	private static int[] toGray(BufferedImage img, int w, int h) {
		int[] rgb = img.getRGB(0, 0, w, h, null, 0, w);
		int[] gray = new int[w * h];
		for (int i = 0; i < gray.length; i++) {
			int p = rgb[i];
			gray[i] = (int) (0.299 * ((p >> 16) & 0xFF) + 0.587 * ((p >> 8) & 0xFF) + 0.114 * (p & 0xFF));
		}
		return gray;
	}

	/** Pixel escuro = bem mais escuro que a média da vizinhança. Funciona com sombra e luz desigual. */
	private static boolean[] localThreshold(int[] gray, int w, int h) {
		int[] integral = new int[(w + 1) * (h + 1)];
		for (int y = 0; y < h; y++) {
			int rowSum = 0;
			for (int x = 0; x < w; x++) {
				rowSum += gray[y * w + x];
				integral[(y + 1) * (w + 1) + (x + 1)] = integral[y * (w + 1) + (x + 1)] + rowSum;
			}
		}
		int r = Math.max(12, w / 16);
		boolean[] dark = new boolean[w * h];
		for (int y = 0; y < h; y++) {
			int y0 = Math.max(0, y - r);
			int y1 = Math.min(h, y + r + 1);
			for (int x = 0; x < w; x++) {
				int x0 = Math.max(0, x - r);
				int x1 = Math.min(w, x + r + 1);
				long sum = (long) integral[y1 * (w + 1) + x1] - integral[y0 * (w + 1) + x1]
						- integral[y1 * (w + 1) + x0] + integral[y0 * (w + 1) + x0];
				double mean = (double) sum / ((x1 - x0) * (y1 - y0));
				dark[y * w + x] = gray[y * w + x] < DARK_RATIO * mean;
			}
		}
		return dark;
	}

	// ---------- marcas dos cantos ----------

	private record Blob(double cx, double cy, int count, int width, int height) {
	}

	/** Devolve os centros das 4 marcas na ordem do layout: topo esquerdo, topo direito, base direita, base esquerda. */
	private double[][] findMarks(boolean[] dark, int w, int h) {
		List<Blob> candidates = components(dark, w, h);
		candidates.sort(Comparator.comparingInt(Blob::count).reversed());
		if (candidates.size() > TOP_CANDIDATES) {
			candidates = new ArrayList<>(candidates.subList(0, TOP_CANDIDATES));
		}
		int n = candidates.size();
		if (n < 4) {
			throw new CardNotFoundException();
		}
		double bestScore = Double.MAX_VALUE;
		double[][] best = null;
		for (int a = 0; a < n; a++) {
			for (int b = a + 1; b < n; b++) {
				for (int c = b + 1; c < n; c++) {
					for (int d = c + 1; d < n; d++) {
						Blob[] quad = { candidates.get(a), candidates.get(b), candidates.get(c), candidates.get(d) };
						double[][] ordered = order(quad);
						if (ordered == null) {
							continue;
						}
						double score = score(ordered, quad, w, h);
						if (score < bestScore) {
							bestScore = score;
							best = ordered;
						}
					}
				}
			}
		}
		if (best == null || bestScore > MAX_QUAD_SCORE) {
			throw new CardNotFoundException();
		}
		return best;
	}

	private List<Blob> components(boolean[] dark, int w, int h) {
		boolean[] seen = new boolean[dark.length];
		int[] stack = new int[dark.length];
		List<Blob> out = new ArrayList<>();
		int minSide = (int) (0.015 * w);
		int maxSide = (int) (0.16 * w);
		for (int start = 0; start < dark.length; start++) {
			if (!dark[start] || seen[start]) {
				continue;
			}
			int sp = 0;
			stack[sp++] = start;
			seen[start] = true;
			int count = 0;
			long sx = 0;
			long sy = 0;
			int minX = Integer.MAX_VALUE;
			int maxX = -1;
			int minY = Integer.MAX_VALUE;
			int maxY = -1;
			while (sp > 0) {
				int p = stack[--sp];
				int x = p % w;
				int y = p / w;
				count++;
				sx += x;
				sy += y;
				minX = Math.min(minX, x);
				maxX = Math.max(maxX, x);
				minY = Math.min(minY, y);
				maxY = Math.max(maxY, y);
				if (x > 0 && dark[p - 1] && !seen[p - 1]) {
					seen[p - 1] = true;
					stack[sp++] = p - 1;
				}
				if (x < w - 1 && dark[p + 1] && !seen[p + 1]) {
					seen[p + 1] = true;
					stack[sp++] = p + 1;
				}
				if (y > 0 && dark[p - w] && !seen[p - w]) {
					seen[p - w] = true;
					stack[sp++] = p - w;
				}
				if (y < h - 1 && dark[p + w] && !seen[p + w]) {
					seen[p + w] = true;
					stack[sp++] = p + w;
				}
			}
			int bw = maxX - minX + 1;
			int bh = maxY - minY + 1;
			double aspect = (double) bw / bh;
			double fill = (double) count / ((double) bw * bh);
			if (bw >= minSide && bh >= minSide && bw <= maxSide && bh <= maxSide
					&& aspect >= 0.6 && aspect <= 1.67 && fill >= 0.45) {
				out.add(new Blob((double) sx / count, (double) sy / count, count, bw, bh));
			}
		}
		return out;
	}

	/**
	 * Ordena 4 pontos em topo esquerdo, topo direito, base direita, base esquerda. Põe os pontos em ordem horária e
	 * escolhe o canto inicial de modo que o lado de cima seja o lado curto da folha (A4 em pé) e fique mais alto na
	 * foto. Vale para folha em pé girada em até uns 80 graus; de cabeça para baixo não dá para distinguir.
	 */
	private static double[][] order(Blob[] q) {
		double mx = 0;
		double my = 0;
		for (Blob b : q) {
			mx += b.cx / 4;
			my += b.cy / 4;
		}
		final double cx = mx;
		final double cy = my;
		Blob[] c = q.clone();
		java.util.Arrays.sort(c, Comparator.comparingDouble(b -> Math.atan2(b.cy - cy, b.cx - cx)));
		int best = -1;
		double bestTopY = Double.MAX_VALUE;
		for (int k = 0; k < 4; k++) {
			double top = Math.hypot(c[k].cx - c[(k + 1) % 4].cx, c[k].cy - c[(k + 1) % 4].cy)
					+ Math.hypot(c[(k + 2) % 4].cx - c[(k + 3) % 4].cx, c[(k + 2) % 4].cy - c[(k + 3) % 4].cy);
			double side = Math.hypot(c[(k + 1) % 4].cx - c[(k + 2) % 4].cx, c[(k + 1) % 4].cy - c[(k + 2) % 4].cy)
					+ Math.hypot(c[(k + 3) % 4].cx - c[k].cx, c[(k + 3) % 4].cy - c[k].cy);
			if (top >= side) {
				continue;
			}
			double topY = c[k].cy + c[(k + 1) % 4].cy;
			if (topY < bestTopY) {
				bestTopY = topY;
				best = k;
			}
		}
		if (best < 0) {
			return null;
		}
		double[][] out = new double[4][];
		for (int i = 0; i < 4; i++) {
			Blob b = c[(best + i) % 4];
			out[i] = new double[] { b.cx, b.cy };
		}
		return out;
	}

	/** Quanto menor, mais o quadrilátero parece o cartão (proporção, lados opostos parecidos, marcas do mesmo tamanho). */
	private static double score(double[][] p, Blob[] quad, int w, int h) {
		double top = dist(p[0], p[1]);
		double bottom = dist(p[3], p[2]);
		double left = dist(p[0], p[3]);
		double right = dist(p[1], p[2]);
		double ratio = (top + bottom) / (left + right);
		int min = Integer.MAX_VALUE;
		int max = 0;
		for (Blob b : quad) {
			min = Math.min(min, b.count);
			max = Math.max(max, b.count);
		}
		double area = Math.abs((p[0][0] * p[1][1] - p[1][0] * p[0][1]) + (p[1][0] * p[2][1] - p[2][0] * p[1][1])
				+ (p[2][0] * p[3][1] - p[3][0] * p[2][1]) + (p[3][0] * p[0][1] - p[0][0] * p[3][1])) / 2.0;
		if (area < 0.12 * w * h) {
			return Double.MAX_VALUE;
		}
		return Math.abs(Math.log(ratio / EXPECTED_RATIO)) + Math.abs(Math.log(top / bottom))
				+ Math.abs(Math.log(left / right)) + 0.5 * Math.log((double) max / min);
	}

	private static double dist(double[] a, double[] b) {
		return Math.hypot(a[0] - b[0], a[1] - b[1]);
	}

	// ---------- bolinhas ----------

	/** Fração de pixels escuros dentro de um disco pequeno no centro da bolinha (longe da borda impressa). */
	private static double darkFraction(boolean[] dark, int w, int h, Homography toImage, double[] centerMm) {
		int total = 0;
		int hit = 0;
		int steps = (int) Math.round(SAMPLE_RADIUS_MM / SAMPLE_STEP_MM);
		for (int iy = -steps; iy <= steps; iy++) {
			for (int ix = -steps; ix <= steps; ix++) {
				double dx = ix * SAMPLE_STEP_MM;
				double dy = iy * SAMPLE_STEP_MM;
				if (dx * dx + dy * dy > SAMPLE_RADIUS_MM * SAMPLE_RADIUS_MM) {
					continue;
				}
				double[] p = toImage.apply(centerMm[0] + dx, centerMm[1] + dy);
				int px = (int) Math.round(p[0]);
				int py = (int) Math.round(p[1]);
				total++;
				if (px >= 0 && px < w && py >= 0 && py < h && dark[py * w + px]) {
					hit++;
				}
			}
		}
		return total == 0 ? 0 : (double) hit / total;
	}
}
