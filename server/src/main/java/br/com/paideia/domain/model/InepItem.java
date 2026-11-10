package br.com.paideia.domain.model;

public record InepItem(
        int questionNumber,
        String correctAlternative,
        double discriminationA,
        double difficultyB,
        double guessingC,
        String topic,
        String skillCode,
        String skillDescription
) {
    public boolean isCorrect(String chosenAlternative) {
        if (chosenAlternative == null || correctAlternative == null) {
            return false;
        }
        return correctAlternative.trim().equalsIgnoreCase(chosenAlternative.trim());
    }

    public String difficultyLevel() {
        if (difficultyB < -0.5) {
            return "EASY";
        } else if (difficultyB <= 1.0) {
            return "MEDIUM";
        } else {
            return "HARD";
        }
    }
}
