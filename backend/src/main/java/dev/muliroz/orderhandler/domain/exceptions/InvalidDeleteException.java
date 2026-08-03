package dev.muliroz.orderhandler.domain.exceptions;

public class InvalidDeleteException extends RuntimeException {
    public InvalidDeleteException(String message) {
        super(message);
    }
}
