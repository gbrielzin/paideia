package br.com.paideia.card;

/** Não foi possível achar as 4 marcas dos cantos na foto. */
public class CardNotFoundException extends RuntimeException {

	public CardNotFoundException() {
		super("Marcas dos cantos não encontradas");
	}
}
