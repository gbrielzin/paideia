package br.com.paideia.service;

import br.com.paideia.domain.model.InepItem;
import br.com.paideia.domain.model.TriScore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TriEngine {

    public static final double ENEM_MEAN = 500.0;
    public static final double ENEM_STD_DEV = 100.0;
    public static final double LOGISTIC_SCALING_D = 1.0;

    private static final double MIN_THETA = -4.0;
    private static final double MAX_THETA = 4.0;
    private static final int QUADRATURE_NODES_COUNT = 81; // nós uniformes entre -4.0 e +4.0 (passo 0.1)

    private final double[] thetaNodes;
    private final double[] priorWeights;

    public TriEngine() {
        this.thetaNodes = new double[QUADRATURE_NODES_COUNT];
        this.priorWeights = new double[QUADRATURE_NODES_COUNT];

        double step = (MAX_THETA - MIN_THETA) / (QUADRATURE_NODES_COUNT - 1);
        double sqrtTwoPi = Math.sqrt(2.0 * Math.PI);

        for (int k = 0; k < QUADRATURE_NODES_COUNT; k++) {
            double theta = MIN_THETA + k * step;
            thetaNodes[k] = theta;
            // Densidade da distribuição a priori Normal Padrão N(0, 1) ponderada pelo passo
            double normalDensity = Math.exp(-0.5 * theta * theta) / sqrtTwoPi;
            priorWeights[k] = normalDensity * step;
        }
    }

    /**
     * Calcula a probabilidade de acerto P_i(theta) segundo o modelo logístico de 3 parâmetros (3PL):
     * P_i(theta) = c_i + (1 - c_i) / (1 + exp(-D * a_i * (theta - b_i)))
     */
    public double calculateProbability(InepItem item, double theta) {
        double a = item.discriminationA();
        double b = item.difficultyB();
        double c = item.guessingC();

        double exponent = -LOGISTIC_SCALING_D * a * (theta - b);
        // Evita overflow/underflow na exponencial
        if (exponent > 50.0) {
            return c;
        } else if (exponent < -50.0) {
            return 1.0;
        }

        double logistic = 1.0 / (1.0 + Math.exp(exponent));
        return c + (1.0 - c) * logistic;
    }

    /**
     * Estima a proficiência latente (theta), a nota na escala oficial do ENEM e o Erro Padrão de Medida (EPM)
     * utilizando o estimador EAP (Expected A Posteriori) com integração numérica.
     */
    public TriScore estimateScore(List<InepItem> items, Map<Integer, Boolean> responses) {
        if (items == null || items.isEmpty()) {
            return new TriScore(0.0, ENEM_MEAN, ENEM_STD_DEV, 0, 0);
        }

        int totalQuestions = items.size();
        int correctCount = 0;
        for (InepItem item : items) {
            Boolean isCorrect = responses != null ? responses.get(item.questionNumber()) : Boolean.FALSE;
            if (Boolean.TRUE.equals(isCorrect)) {
                correctCount++;
            }
        }

        // 1. Calcula o log da verossimilhança ln L(theta_k) para cada nó de quadratura
        double[] logLikelihood = new double[QUADRATURE_NODES_COUNT];
        double maxLogLikelihood = Double.NEGATIVE_INFINITY;

        for (int k = 0; k < QUADRATURE_NODES_COUNT; k++) {
            double theta = thetaNodes[k];
            double logL = 0.0;

            for (InepItem item : items) {
                double prob = calculateProbability(item, theta);
                // Delimita probabilidades para evitar ln(0)
                prob = Math.max(1e-12, Math.min(1.0 - 1e-12, prob));

                Boolean isCorrect = responses != null ? responses.get(item.questionNumber()) : Boolean.FALSE;
                if (Boolean.TRUE.equals(isCorrect)) {
                    logL += Math.log(prob);
                } else {
                    logL += Math.log(1.0 - prob);
                }
            }

            logLikelihood[k] = logL;
            if (logL > maxLogLikelihood) {
                maxLogLikelihood = logL;
            }
        }

        // 2. Calcula a densidade a posteriori ponderada normalizada
        double posteriorSum = 0.0;
        double[] unnormalizedPosterior = new double[QUADRATURE_NODES_COUNT];

        for (int k = 0; k < QUADRATURE_NODES_COUNT; k++) {
            double likelihood = Math.exp(logLikelihood[k] - maxLogLikelihood);
            double posterior = likelihood * priorWeights[k];
            unnormalizedPosterior[k] = posterior;
            posteriorSum += posterior;
        }

        if (posteriorSum <= 0.0) {
            posteriorSum = 1e-12;
        }

        // 3. Estimação EAP: E[theta | U] (média ponderada a posteriori)
        double estimatedTheta = 0.0;
        for (int k = 0; k < QUADRATURE_NODES_COUNT; k++) {
            double normalizedPosterior = unnormalizedPosterior[k] / posteriorSum;
            estimatedTheta += thetaNodes[k] * normalizedPosterior;
        }

        // 4. Cálculo do Erro Padrão de Medida (desvio padrão da distribuição a posteriori)
        double variance = 0.0;
        for (int k = 0; k < QUADRATURE_NODES_COUNT; k++) {
            double normalizedPosterior = unnormalizedPosterior[k] / posteriorSum;
            double diff = thetaNodes[k] - estimatedTheta;
            variance += diff * diff * normalizedPosterior;
        }

        double standardErrorTheta = Math.sqrt(Math.max(1e-6, variance));

        // 5. Transformação linear para a escala oficial do ENEM
        double estimatedScore = ENEM_MEAN + (ENEM_STD_DEV * estimatedTheta);
        double standardErrorScore = ENEM_STD_DEV * standardErrorTheta;

        // Arredondamento com precisão de 1 casa decimal (padrão ENEM)
        double roundedScore = Math.round(estimatedScore * 10.0) / 10.0;
        double roundedSE = Math.round(standardErrorScore * 10.0) / 10.0;
        double roundedTheta = Math.round(estimatedTheta * 1000.0) / 1000.0;

        return new TriScore(roundedTheta, roundedScore, roundedSE, totalQuestions, correctCount);
    }
}
