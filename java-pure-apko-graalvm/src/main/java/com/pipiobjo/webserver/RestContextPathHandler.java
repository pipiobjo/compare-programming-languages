package com.pipiobjo.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import com.pipiobjo.webserver.errors.ApplicationExceptions;
import com.pipiobjo.webserver.errors.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipiobjo.dependencyinjection.BeanHandler;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.pipiobjo.app.Context;

import io.vavr.control.Try;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public abstract class RestContextPathHandler implements BeanHandler {

    private ObjectMapper objectMapper;
    private GlobalExceptionHandler exceptionHandler;

    private static String decode(final String encoded) {
        return Optional.ofNullable(encoded)
                .map(e -> URLDecoder.decode(e, StandardCharsets.UTF_8))
                .orElse(null);
    }


    protected void loadContextGeneric(Context ctx){
        this.objectMapper = ctx.getOBJECT_MAPPER();
        this.exceptionHandler = ctx.getGLOBAL_ERROR_HANDLER();
    }

    public void handle(HttpExchange exchange) {
        Try.run(() -> execute(exchange))
            .onFailure(thr -> exceptionHandler.handle(thr, exchange));
    }

    protected abstract void execute(HttpExchange exchange) throws Exception;


    protected <T> T readRequest(InputStream is, Class<T> type) {
        return Try.of(() -> objectMapper.readValue(is, type))
            .getOrElseThrow(ApplicationExceptions.invalidRequest());
    }

    protected <T> byte[] writeResponse(T response) {
        return Try.of(() -> objectMapper.writeValueAsBytes(response))
            .getOrElseThrow(ApplicationExceptions.invalidRequest());
    }



    protected boolean isHttpGet(HttpExchange exchange) {
        return "GET".equals(exchange.getRequestMethod());
    }

    protected boolean isHttpPost(HttpExchange exchange) {
        return "POST".equals(exchange.getRequestMethod());
    }
    protected byte[] buildByteRespone(HttpExchange exchange, ResponseEntity entity) throws IOException {
        byte[] response;
        exchange.getResponseHeaders().putAll(entity.getHeaders());
        exchange.sendResponseHeaders(entity.getStatusCode().getCode(), 0);
        response = writeResponse(entity.getBody());
        return response;
    }

//    protected Map<String, List<String>> getURLQueryParams(String query) {
//        if(query == null || query.equals("")){
//            return Collections.emptyMap();
//        }
//        return Pattern.compile("&")
//                .splitAsStream(query)
//                .map(s -> Arrays.copyOf(s.split("=", 2), 2))
//                .collect(groupingBy(s -> RestContextPathHandler.decode(s[0]), mapping(s -> RestContextPathHandler.decode(s[1]), toList())));
//    }
}
