package dev.muliroz.orderhandler.domain.exceptions;

public class ResourceNotExistsException extends RuntimeException {
    public ResourceNotExistsException(String message) {
        super(message);
    }
}
