package br.com.paideia.card;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

/**
 * Gera "fotos" sintéticas do cartão preenchido, com respostas conhecidas, para testar o leitor.
 * Desenha o cartão plano, entorta pela perspectiva e aplica luz desigual, sombra, desfoque, ruído e JPEG.
 * Usa as mesmas medidas do layout do leitor: não testa diferenças entre o PDF impresso e o layout.
 */
public final class SyntheticCard {

	static final double PX_PER_MM = 8.0;
	static final int PHOTO_W = 1500;
	static final int PHOTO_H = 2000;

	/** Condições da "foto". Tudo em valores simples; 0 significa sem o efeito. */
	public record Conditions(
			double rotationDeg,
			double tilt,
			double lightGradient,
			boolean shadow,
			int blurRadius,
			double noiseSigma,
			int penGray,
			double cardWidthFraction) {

		/** penGray 0 = caneta preta (cinza 20 a 70); outro valor = caneta mais clara (cinza de penGray a penGray + 40). */
		public static Conditions clean() {
			return new Conditions(0, 0, 0, false, 0, 0, 0, 0.78);
		}
	}

	private SyntheticCard() {
	}

	/** truth: 45 caracteres, letra A a E ou "." para branco. */
	public static byte[] photo(CardLayout layout, String truth, Conditions c, Random rng) {
		BufferedImage flat = flatCard(layout, truth, c, rng);
		BufferedImage photo = warp(flat, layout, c, rng);
		return jpeg(photo);
	}

	private static BufferedImage flatCard(CardLayout layout, String truth, Conditions c, Random rng) {
		int w = (int) Math.round(layout.pageWidthMm() * PX_PER_MM);
		int h = (int) Math.round(layout.pageHeightMm() * PX_PER_MM);
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);

		g.setColor(Color.BLACK);
		double ms = layout.markSizeMm();
		for (double[] m : layout.markCenters()) {
			g.fillRect(px(m[0] - ms / 2), px(m[1] - ms / 2), px(ms), px(ms));
		}
		// texto de instruções, parecido com o do PDF
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, px(3.0)));
		for (int i = 0; i < 5; i++) {
			g.drawString("Instrução de exemplo para preencher o cartão com caneta preta", px(24), px(44 + i * 5.2));
		}
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, px(3.2)));
		g.setStroke(new BasicStroke((float) (0.45 * PX_PER_MM)));
		double r = layout.bubbleDiameterMm() / 2;
		for (int q = 0; q < layout.questions(); q++) {
			double[] first = layout.bubbleCenterMm(q, 0);
			g.drawString(String.valueOf(layout.firstPosition() + q), px(first[0] - 13), px(first[1] + 1.2));
			for (int l = 0; l < layout.letters().length(); l++) {
				double[] b = layout.bubbleCenterMm(q, l);
				g.setColor(Color.BLACK);
				g.draw(new Ellipse2D.Double((b[0] - r) * PX_PER_MM, (b[1] - r) * PX_PER_MM, 2 * r * PX_PER_MM, 2 * r * PX_PER_MM));
			}
			char answer = truth.charAt(q);
			int idx = answer == '.' ? -1 : layout.letters().indexOf(answer);
			if (idx >= 0) {
				double[] b = layout.bubbleCenterMm(q, idx);
				double fr = r * (0.80 + 0.20 * rng.nextDouble());
				double dx = (rng.nextDouble() - 0.5) * 0.8;
				double dy = (rng.nextDouble() - 0.5) * 0.8;
				int gray = c.penGray() > 0 ? c.penGray() + rng.nextInt(40) : 20 + rng.nextInt(50);
				g.setColor(new Color(gray, gray, gray));
				g.fill(new Ellipse2D.Double((b[0] + dx - fr) * PX_PER_MM, (b[1] + dy - fr) * PX_PER_MM,
						2 * fr * PX_PER_MM, 2 * fr * PX_PER_MM));
			}
		}
		g.dispose();
		return img;
	}

	private static int px(double mm) {
		return (int) Math.round(mm * PX_PER_MM);
	}

	private static BufferedImage warp(BufferedImage flat, CardLayout layout, Conditions c, Random rng) {
		int fw = flat.getWidth();
		int fh = flat.getHeight();
		double cardW = c.cardWidthFraction() * PHOTO_W;
		double cardH = cardW * layout.pageHeightMm() / layout.pageWidthMm();
		double ang = Math.toRadians(c.rotationDeg());
		double cx = PHOTO_W / 2.0 + (rng.nextDouble() - 0.5) * 0.06 * PHOTO_W;
		double cy = PHOTO_H / 2.0 + (rng.nextDouble() - 0.5) * 0.06 * PHOTO_H;
		double[][] local = { { -cardW / 2, -cardH / 2 }, { cardW / 2, -cardH / 2 }, { cardW / 2, cardH / 2 },
				{ -cardW / 2, cardH / 2 } };
		double[][] quad = new double[4][2];
		for (int i = 0; i < 4; i++) {
			double x = local[i][0] + (rng.nextDouble() * 2 - 1) * c.tilt() * cardW;
			double y = local[i][1] + (rng.nextDouble() * 2 - 1) * c.tilt() * cardW;
			quad[i][0] = cx + x * Math.cos(ang) - y * Math.sin(ang);
			quad[i][1] = cy + x * Math.sin(ang) + y * Math.cos(ang);
		}
		double[][] flatCorners = { { 0, 0 }, { fw, 0 }, { fw, fh }, { 0, fh } };
		Homography photoToFlat = Homography.from(quad, flatCorners);

		double lightAngle = rng.nextDouble() * Math.PI * 2;
		double shadowAngle = rng.nextDouble() * Math.PI;
		double shadowOffset = (rng.nextDouble() - 0.5) * 0.6 * PHOTO_W;
		int[] flatPix = flat.getRGB(0, 0, fw, fh, null, 0, fw);
		float[][] rgb = new float[3][PHOTO_W * PHOTO_H];
		for (int y = 0; y < PHOTO_H; y++) {
			for (int x = 0; x < PHOTO_W; x++) {
				double[] f = photoToFlat.apply(x, y);
				double r;
				double gC;
				double b;
				if (f[0] >= 0 && f[0] < fw - 1 && f[1] >= 0 && f[1] < fh - 1) {
					int x0 = (int) f[0];
					int y0 = (int) f[1];
					double tx = f[0] - x0;
					double ty = f[1] - y0;
					double[] acc = new double[3];
					for (int k = 0; k < 4; k++) {
						int xx = x0 + (k & 1);
						int yy = y0 + (k >> 1);
						double wgt = ((k & 1) == 1 ? tx : 1 - tx) * ((k >> 1) == 1 ? ty : 1 - ty);
						int p = flatPix[yy * fw + xx];
						acc[0] += wgt * ((p >> 16) & 0xFF);
						acc[1] += wgt * ((p >> 8) & 0xFF);
						acc[2] += wgt * (p & 0xFF);
					}
					r = acc[0];
					gC = acc[1];
					b = acc[2];
				}
				else {
					r = 96;
					gC = 74;
					b = 58;
				}
				double light = 1.0 + c.lightGradient() * (((x * Math.cos(lightAngle) + y * Math.sin(lightAngle)) / PHOTO_W) - 0.5);
				if (c.shadow()) {
					double d = (x * Math.sin(shadowAngle) - y * Math.cos(shadowAngle) - shadowOffset) / (0.12 * PHOTO_W);
					double band = Math.exp(-d * d);
					light *= 1.0 - 0.45 * band;
				}
				light = Math.max(0.25, Math.min(1.1, light * 0.92));
				int i = y * PHOTO_W + x;
				rgb[0][i] = (float) (r * light);
				rgb[1][i] = (float) (gC * light);
				rgb[2][i] = (float) (b * light);
			}
		}
		if (c.blurRadius() > 0) {
			for (float[] ch : rgb) {
				boxBlur(ch, PHOTO_W, PHOTO_H, c.blurRadius());
			}
		}
		BufferedImage out = new BufferedImage(PHOTO_W, PHOTO_H, BufferedImage.TYPE_INT_RGB);
		int[] outPix = new int[PHOTO_W * PHOTO_H];
		for (int i = 0; i < outPix.length; i++) {
			double n = c.noiseSigma() > 0 ? rng.nextGaussian() * c.noiseSigma() : 0;
			int rr = clamp(rgb[0][i] + n);
			int gg = clamp(rgb[1][i] + n);
			int bb = clamp(rgb[2][i] + n);
			outPix[i] = (rr << 16) | (gg << 8) | bb;
		}
		out.setRGB(0, 0, PHOTO_W, PHOTO_H, outPix, 0, PHOTO_W);
		return out;
	}

	private static int clamp(double v) {
		return (int) Math.max(0, Math.min(255, Math.round(v)));
	}

	private static void boxBlur(float[] ch, int w, int h, int radius) {
		float[] tmp = new float[ch.length];
		int span = 2 * radius + 1;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				float s = 0;
				for (int k = -radius; k <= radius; k++) {
					s += ch[y * w + Math.max(0, Math.min(w - 1, x + k))];
				}
				tmp[y * w + x] = s / span;
			}
		}
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				float s = 0;
				for (int k = -radius; k <= radius; k++) {
					s += tmp[Math.max(0, Math.min(h - 1, y + k)) * w + x];
				}
				ch[y * w + x] = s / span;
			}
		}
	}

	private static byte[] jpeg(BufferedImage img) {
		try {
			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(0.85f);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try (MemoryCacheImageOutputStream out = new MemoryCacheImageOutputStream(bos)) {
				writer.setOutput(out);
				writer.write(null, new IIOImage(img, null, null), param);
			}
			writer.dispose();
			return bos.toByteArray();
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
