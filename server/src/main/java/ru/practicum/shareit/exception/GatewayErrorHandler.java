package ru.practicum.shareit.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

@RestControllerAdvice
public class GatewayErrorHandler {

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<String> handleHttpError(HttpStatusCodeException e) {
        // Возвращаем оригинальный статус и тело от Server
        return ResponseEntity.status(e.getStatusCode())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(e.getResponseBodyAsString());
    }
}