package br.com.paideia.card;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/** Valida e decodifica a foto na memória. Nada é gravado em disco. */
public final class ImageDecoder {

	/** Tamanho máximo do arquivo enviado (o limite do multipart no application.properties é o mesmo). */
	public static final int MAX_BYTES = 8 * 1024 * 1024;
	/** Evita imagem minúscula em bytes mas enorme depois de aberta. */
	static final long MAX_PIXELS = 40_000_000L;

	static {
		// Sem cache em arquivo temporário: a decodificação usa só memória.
		ImageIO.setUseCache(false);
	}

	private ImageDecoder() {
	}

	public static BufferedImage decode(byte[] data) {
		if (data == null || data.length == 0 || data.length > MAX_BYTES || !isJpegOrPng(data)) {
			throw new InvalidImageException();
		}
		try (var in = ImageIO.createImageInputStream(new ByteArrayInputStream(data))) {
			var readers = ImageIO.getImageReaders(in);
			if (!readers.hasNext()) {
				throw new InvalidImageException();
			}
			var reader = readers.next();
			try {
				reader.setInput(in, true, true);
				long pixels = (long) reader.getWidth(0) * reader.getHeight(0);
				if (pixels > MAX_PIXELS) {
					throw new InvalidImageException();
				}
				BufferedImage img = reader.read(0);
				if (img == null) {
					throw new InvalidImageException();
				}
				return img;
			}
			finally {
				reader.dispose();
			}
		}
		catch (IOException | RuntimeException e) {
			if (e instanceof InvalidImageException invalid) {
				throw invalid;
			}
			throw new InvalidImageException();
		}
	}

	private static boolean isJpegOrPng(byte[] d) {
		boolean jpeg = d.length > 3 && (d[0] & 0xFF) == 0xFF && (d[1] & 0xFF) == 0xD8 && (d[2] & 0xFF) == 0xFF;
		boolean png = d.length > 8 && (d[0] & 0xFF) == 0x89 && d[1] == 'P' && d[2] == 'N' && d[3] == 'G';
		return jpeg || png;
	}

	/** Arquivo ausente, grande demais, de outro tipo ou ilegível. */
	public static class InvalidImageException extends RuntimeException {
		public InvalidImageException() {
			super("Imagem inválida");
		}
	}
}
