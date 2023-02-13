package com.pipiobjo.webserver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EndpointRegistry {
    public List<Endpoint> endpoints = new ArrayList<>();

    public void registerEndpoint(Endpoint endpoint) {
        endpoints.add(endpoint);
    }

    public List<Endpoint> findEndpoint(String httpMethod, String path){

        return endpoints
                .stream()
                .filter(endpoint -> endpoint.isResponsible(httpMethod, path))
                .collect(Collectors.toList());


    }
}
