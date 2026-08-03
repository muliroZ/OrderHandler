package dev.muliroz.orderhandler.domain.exceptions;

public class InvalidUpdateException extends RuntimeException {
    public InvalidUpdateException(String message) {
        super(message);
    }
}
