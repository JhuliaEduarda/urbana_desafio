package br.com.urbana.exception;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException() {
        super("Card not found");
    }
}