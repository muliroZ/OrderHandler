package dev.muliroz.orderhandler.domain.exceptions;

public class InvalidNumberException extends RuntimeException {
    public InvalidNumberException(String message) {
        super(message);
    }
}
