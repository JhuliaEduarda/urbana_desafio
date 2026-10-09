package br.com.urbana.exception;

public class CardNumberAlreadyExistsException extends RuntimeException {

    public CardNumberAlreadyExistsException() {
        super("Card number already exists");
    }
}