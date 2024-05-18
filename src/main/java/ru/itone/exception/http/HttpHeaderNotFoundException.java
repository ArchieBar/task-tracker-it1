package ru.itone.exception.http;

public class HttpHeaderNotFoundException extends RuntimeException {
    public HttpHeaderNotFoundException(String header) {
        super(String.format("Заголовок '%s' не найден в запросе.", header));
    }
}
