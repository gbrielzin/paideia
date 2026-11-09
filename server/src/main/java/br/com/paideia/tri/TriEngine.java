package br.com.paideia.tri;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Motor da TRI (modelo logístico de 3 parâmetros, estimativa EAP). Sem estado: só lê a prova.
 * As constantes e a fórmula precisam ser iguais às de data/tri_reference.py (ver data/README.md).
 */
public class TriEngine {

	static final double GRID_MIN = -4.0;
	static final double GRID_MAX = 6.0;
	static final double GRID_STEP = 0.01;
	static final double SCALE_MEAN = 500.0;
	static final double SCALE_SD = 129.7;
	static final double D = 1.0;

	private final Exam exam;
	private final double[] grid;
	private final double[] logPrior;
	/** logRight[i][k]: log da chance de acertar o item i no ponto k da grade. Só para itens que contam. */
	private final double[][] logRight;
	private final double[][] logWrong;

	public TriEngine(Exam exam) {
		this.exam = exam;
		int n = (int) Math.round((GRID_MAX - GRID_MIN) / GRID_STEP) + 1;
		grid = new double[n];
		logPrior = new double[n];
		for (int k = 0; k < n; k++) {
			grid[k] = GRID_MIN + k * GRID_STEP;
			logPrior[k] = -0.5 * grid[k] * grid[k];
		}
		int items = exam.size();
		logRight = new double[items][];
		logWrong = new double[items][];
		for (int i = 0; i < items; i++) {
			ExamItem item = exam.items().get(i);
			if (item.annulled()) {
				continue;
			}
			logRight[i] = new double[n];
			logWrong[i] = new double[n];
			for (int k = 0; k < n; k++) {
				double p = probCorrect(grid[k], item.a(), item.b(), item.c());
				logRight[i][k] = Math.log(p);
				logWrong[i][k] = Math.log(1.0 - p);
			}
		}
	}

	public int size() {
		return exam.size();
	}

	static double probCorrect(double theta, double a, double b, double c) {
		return c + (1.0 - c) / (1.0 + Math.exp(-D * a * (theta - b)));
	}

	/**
	 * Calcula nota, erro e mapa de erros. Qualquer letra diferente do gabarito (branco, inválida, errada) conta como erro.
	 *
	 * @param answers uma letra por questão, na ordem da prova
	 */
	public Analysis analyze(String answers) {
		if (answers == null || answers.length() != exam.size()) {
			throw new IllegalArgumentException("São esperadas " + exam.size() + " respostas");
		}
		int items = exam.size();
		boolean[] correct = new boolean[items];
		double[] logPost = logPrior.clone();
		for (int i = 0; i < items; i++) {
			ExamItem item = exam.items().get(i);
			if (item.annulled()) {
				continue;
			}
			correct[i] = answers.substring(i, i + 1).equalsIgnoreCase(item.answerKey());
			double[] table = correct[i] ? logRight[i] : logWrong[i];
			for (int k = 0; k < logPost.length; k++) {
				logPost[k] += table[k];
			}
		}
		double[] base = summarize(logPost);

		List<ErrorCost> errors = new ArrayList<>();
		for (int i = 0; i < items; i++) {
			ExamItem item = exam.items().get(i);
			if (item.annulled() || correct[i]) {
				continue;
			}
			double[] flipped = new double[logPost.length];
			for (int k = 0; k < flipped.length; k++) {
				flipped[k] = logPost[k] - logWrong[i][k] + logRight[i][k];
			}
			double newScore = summarize(flipped)[0];
			errors.add(new ErrorCost(item.position(), item.skill(), newScore, newScore - base[0]));
		}
		errors.sort(Comparator.comparingDouble(ErrorCost::cost).reversed().thenComparingInt(ErrorCost::position));
		return new Analysis(base[0], base[1], List.copyOf(errors));
	}

	/** Média e desvio da distribuição a posteriori, já na escala do ENEM: {nota, erro}. */
	private double[] summarize(double[] logPost) {
		double max = Double.NEGATIVE_INFINITY;
		for (double v : logPost) {
			max = Math.max(max, v);
		}
		double total = 0;
		double sum = 0;
		double[] w = new double[logPost.length];
		for (int k = 0; k < w.length; k++) {
			w[k] = Math.exp(logPost[k] - max);
			total += w[k];
			sum += w[k] * grid[k];
		}
		double mean = sum / total;
		double var = 0;
		for (int k = 0; k < w.length; k++) {
			double d = grid[k] - mean;
			var += w[k] * d * d;
		}
		var /= total;
		return new double[] { SCALE_MEAN + SCALE_SD * mean, SCALE_SD * Math.sqrt(var) };
	}
}
