package dev.muliroz.orderhandler.controller;

import dev.muliroz.orderhandler.domain.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmptyOrderException.class)
    public ResponseEntity<String> handleEmptyOrderException(EmptyOrderException ex) {
        return ResponseEntity.badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidNumberException.class)
    public ResponseEntity<String> handleInvalidNumberException(InvalidNumberException ex) {
        return ResponseEntity.badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotExistsException.class)
    public ResponseEntity<String> handleResourceNotExistsException(ResourceNotExistsException ex) {
        return ResponseEntity.status(404)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<String> handleResourceExistsException(ResourceExistsException ex) {
        return ResponseEntity.status(409)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidUpdateException.class)
    public ResponseEntity<String> handleInvalidUpdateException(InvalidUpdateException ex) {
        return ResponseEntity.status(400)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidDeleteException.class)
    public ResponseEntity<String> handleInvalidDeleteException(InvalidDeleteException ex) {
        return ResponseEntity.status(409)
                .body(ex.getMessage());
    }
}
