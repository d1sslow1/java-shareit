package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Accept-Encoding", "identity"); // Отключаем сжатие

        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response;
            if (parameters != null && !parameters.isEmpty()) {
                response = rest.exchange(path, method, requestEntity, String.class, parameters);
            } else {
                response = rest.exchange(path, method, requestEntity, String.class);
            }

            // Создаем новый ответ с правильными заголовками
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            responseHeaders.setContentLength(response.getBody() != null ? response.getBody().getBytes().length : 0);

            return new ResponseEntity<>(
                    response.getBody(),
                    responseHeaders,
                    response.getStatusCode()
            );

        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getResponseBodyAsString());
        }
    }
}