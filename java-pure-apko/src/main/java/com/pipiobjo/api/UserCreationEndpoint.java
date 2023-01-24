package com.pipiobjo.api;

import com.pipiobjo.app.Context;
import com.pipiobjo.domain.user.NewUser;
import com.pipiobjo.domain.user.UserAlreadyException;
import com.pipiobjo.webserver.Constants;
import com.pipiobjo.webserver.Endpoint;
import com.pipiobjo.webserver.ResponseEntity;
import com.pipiobjo.webserver.StatusCode;
import com.pipiobjo.webserver.errors.InvalidRequestException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpPrincipal;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import java.net.URI;
@Slf4j
public class UserCreationEndpoint implements Endpoint {

    private final Context ctx;

    public UserCreationEndpoint(Context ctx){
        this.ctx = ctx;

    }
    @Override
    public boolean isResponsible(String httpMethod, String path) {
        if("GET".equals(httpMethod)){
            log.info("path={}", path);
        }
        if("POST".equals(httpMethod) && path.startsWith("/api/user")){
            return true;
        }
        return false;
    }

    @Override
    public Class<RegistrationRequest> getPostBodyClass(){
        return RegistrationRequest.class;
    }


    private static Headers getHeaders(String key, String value) {
        Headers headers = new Headers();
        headers.set(key, value);
        return headers;
    }

    @Override
    public ResponseEntity doPost(Object o, URI requestURI, HttpPrincipal userPrincipal) {
        RegistrationRequest postBody = (RegistrationRequest) o;
        NewUser user = NewUser.builder()
            .login(postBody.getLogin())
            .password(PasswordEncoder.encode(postBody.getPassword()))
            .firstName(postBody.getFirstname())
            .lastName(postBody.getLastname())
            .build();
        String userId = null;
        try {
            userId = ctx.getUSER_SERVICE().create(user);
        } catch (UserAlreadyException e) {
//            throw new RuntimeException(e);
            return new ResponseEntity<>("user already exists",
                    getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.BAD_REQUEST);
        }

        RegistrationResponse response = new RegistrationResponse(userId);

        return new ResponseEntity<>(response,
            getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }

    @Override
    public ResponseEntity doGet(URI requestURI, HttpPrincipal userPrincipal) {
        throw new InvalidRequestException(HttpURLConnection.HTTP_NOT_IMPLEMENTED, "");
    }


}
