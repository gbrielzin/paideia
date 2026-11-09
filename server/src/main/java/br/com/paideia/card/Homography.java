package br.com.paideia.card;

/** Transformação de perspectiva entre dois planos, definida por 4 pares de pontos. */
public final class Homography {

	private final double[] h; // h[0..7]; h33 = 1

	private Homography(double[] h) {
		this.h = h;
	}

	/** Cria a transformação que leva cada src[i] em dst[i] (4 pontos {x, y}). */
	public static Homography from(double[][] src, double[][] dst) {
		double[][] a = new double[8][9];
		for (int i = 0; i < 4; i++) {
			double x = src[i][0];
			double y = src[i][1];
			double u = dst[i][0];
			double v = dst[i][1];
			a[2 * i] = new double[] { x, y, 1, 0, 0, 0, -u * x, -u * y, u };
			a[2 * i + 1] = new double[] { 0, 0, 0, x, y, 1, -v * x, -v * y, v };
		}
		return new Homography(solve(a));
	}

	public double[] apply(double x, double y) {
		double w = h[6] * x + h[7] * y + 1.0;
		return new double[] { (h[0] * x + h[1] * y + h[2]) / w, (h[3] * x + h[4] * y + h[5]) / w };
	}

	/** Eliminação de Gauss com pivô parcial em um sistema 8x8 (matriz aumentada 8x9). */
	private static double[] solve(double[][] a) {
		int n = 8;
		for (int col = 0; col < n; col++) {
			int pivot = col;
			for (int r = col + 1; r < n; r++) {
				if (Math.abs(a[r][col]) > Math.abs(a[pivot][col])) {
					pivot = r;
				}
			}
			double[] tmp = a[col];
			a[col] = a[pivot];
			a[pivot] = tmp;
			if (Math.abs(a[col][col]) < 1e-12) {
				throw new IllegalArgumentException("Pontos alinhados: não há transformação");
			}
			for (int r = col + 1; r < n; r++) {
				double f = a[r][col] / a[col][col];
				for (int c = col; c <= n; c++) {
					a[r][c] -= f * a[col][c];
				}
			}
		}
		double[] x = new double[n];
		for (int r = n - 1; r >= 0; r--) {
			double s = a[r][n];
			for (int c = r + 1; c < n; c++) {
				s -= a[r][c] * x[c];
			}
			x[r] = s / a[r][r];
		}
		return x;
	}
}
