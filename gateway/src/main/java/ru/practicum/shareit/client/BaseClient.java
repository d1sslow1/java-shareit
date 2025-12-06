package ru.practicum.shareit.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected ResponseEntity<Object> post(String path, Object body) {
        return post(path, null, body);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, null, body);
    }

    protected ResponseEntity<Object> patch(String path) {
        return patch(path, null, null);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, null, body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, null);
    }

    protected ResponseEntity<Object> put(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, null, body);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, null, null);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                      @Nullable Map<String, Object> parameters,
                                                      @Nullable Object body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            if (userId != null) {
                headers.set("X-Sharer-User-Id", String.valueOf(userId));
            }

            HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response;
            if (parameters != null && !parameters.isEmpty()) {
                response = rest.exchange(path, method, requestEntity, String.class, parameters);
            } else {
                response = rest.exchange(path, method, requestEntity, String.class);
            }

            // Парсим JSON ответ
            Object responseBody = null;
            if (response.getBody() != null && !response.getBody().isEmpty()) {
                try {
                    responseBody = objectMapper.readValue(response.getBody(), Object.class);
                } catch (Exception e) {
                    responseBody = response.getBody();
                }
            }

            // Возвращаем ResponseEntity с оригинальным статусом
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(response.getStatusCode())
                    .headers(responseHeaders)
                    .body(responseBody);

        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            // ВАЖНО: При ошибке возвращаем оригинальный статус код и тело

            Object errorBody;
            try {
                errorBody = objectMapper.readValue(e.getResponseBodyAsString(), Object.class);
            } catch (Exception ex) {
                errorBody = e.getResponseBodyAsString();
            }

            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(e.getStatusCode())
                    .headers(errorHeaders)
                    .body(errorBody);

        } catch (Exception e) {
            // Только для неожиданных ошибок возвращаем 500
            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.setContentType(MediaType.APPLICATION_JSON);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(errorHeaders)
                    .body(Map.of("error", "Gateway internal error: " + e.getMessage()));
        }
    }
}