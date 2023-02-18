package com.pipiobjo.webserver;

import com.sun.net.httpserver.HttpPrincipal;

import java.net.URI;

public interface Endpoint {
    public boolean isResponsible(String httpMethod, String path);

    Class getPostBodyClass();

    ResponseEntity doPost(Object postBody, URI requestURI, HttpPrincipal userPrincipal);

    ResponseEntity doGet(URI requestURI, HttpPrincipal userPrincipal);
}
