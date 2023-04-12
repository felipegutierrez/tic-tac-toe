package com.adsquare.tictactoe.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handleRequestBodyError(WebExchangeBindException exception) {
        log.error("Exception caught in handleRequestBodyError: {}", exception.getMessage(), exception);
        var error = exception.getBindingResult()
            .getAllErrors()
            .stream()
            .map(objectError -> (objectError.getDefaultMessage() != null ? objectError.getDefaultMessage() : ""))
            .sorted()
            .collect(Collectors.joining(", "));
        log.error("Error is: {}", error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    @ExceptionHandler(BoardException.class)
    public ResponseEntity<String> handleBoardException(BoardException ex) {
        log.error("Error is: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ex.getMessage());
    }

    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<String> handleBoardNotFoundException(BoardNotFoundException ex) {
        log.error("Error is: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ex.getMessage());
    }
}
