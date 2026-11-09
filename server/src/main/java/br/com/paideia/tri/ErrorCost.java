package br.com.paideia.tri;

/** Um erro do aluno: quanto a nota subiria se esta questão estivesse certa. */
public record ErrorCost(int position, int skill, double scoreIfCorrect, double cost) {
}
