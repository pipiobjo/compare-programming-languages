package com.pipiobjo.api;

import com.pipiobjo.app.Context;
import com.pipiobjo.domain.greeting.GreetingMessage;
import com.pipiobjo.domain.user.User;
import com.pipiobjo.domain.user.UserSearchParams;
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
import java.util.List;

@Slf4j
public class GreetingEndpoint implements Endpoint {

    private final Context ctx;

    public GreetingEndpoint(Context ctx){
        this.ctx = ctx;
    }
    @Override
    public boolean isResponsible(String httpMethod, String path) {
        if ("GET".equals(httpMethod) && path.startsWith("/api/greeting")) {
            return true;
        }
        return false;
    }

    @Override
    public Class getPostBodyClass() {
        throw new InvalidRequestException(HttpURLConnection.HTTP_NOT_IMPLEMENTED, "");
    }

    @Override
    public ResponseEntity doPost(Object postBody, URI requestURI, HttpPrincipal userPrincipal) {
        throw new InvalidRequestException(HttpURLConnection.HTTP_NOT_IMPLEMENTED, "");
    }

    @Override
    public ResponseEntity doGet(URI requestURI, HttpPrincipal userPrincipal) {

        if(userPrincipal != null){
            String login = userPrincipal.getUsername();
            UserSearchParams userSearchParams = UserSearchParams.builder().login(login).build();
            List<User> users = ctx.getUSER_SERVICE().getUsers(userSearchParams);
            if(users.size()>1){
                log.error("Found multiple users but expected only one for a login={}", login);
                throw new InvalidRequestException(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
            }
            User user = users.get(0);
            String greetingMessage = String.format("Hello %s %s!", user.getFirstName(), user.getLastName());
            GreetingMessage result = GreetingMessage.builder().msg(greetingMessage).firstname(user.getFirstName()).lastname(user.getLastName()).build();
            return new ResponseEntity<>(result,
                    getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);

        }
        throw new InvalidRequestException(HttpURLConnection.HTTP_FORBIDDEN, "");
    }

    private static Headers getHeaders(String key, String value) {
        Headers headers = new Headers();
        headers.set(key, value);
        return headers;
    }
}
