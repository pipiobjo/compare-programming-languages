package com.pipiobjo.api;

import com.pipiobjo.app.Context;
import com.pipiobjo.domain.user.UiUser;
import com.pipiobjo.domain.user.User;
import com.pipiobjo.domain.user.UserSearchParams;
import com.pipiobjo.webserver.*;
import com.pipiobjo.webserver.errors.InvalidRequestException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpPrincipal;
import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserListEndpoint implements Endpoint {

    private final Context ctx;

    public UserListEndpoint(Context ctx) {
        this.ctx = ctx;

    }

    @Override
    public boolean isResponsible(String httpMethod, String path) {
        if ("GET".equals(httpMethod) && path.startsWith("/api/user")) {
            return true;
        }
        return false;
    }

    @Override
    public Class getPostBodyClass() {
        return null;
    }

    @Override
    public ResponseEntity doPost(Object postBody, URI requestURI, HttpPrincipal userPrincipal) {
        throw new InvalidRequestException(HttpURLConnection.HTTP_NOT_IMPLEMENTED, "");
    }

    private static Headers getHeaders(String key, String value) {
        Headers headers = new Headers();
        headers.set(key, value);
        return headers;
    }

    @Override
    public ResponseEntity doGet(URI requestURI, HttpPrincipal userName) {


        UserSearchParams.UserSearchParamsBuilder builder = UserSearchParams.builder();
        Map<String, List<String>> params = HTTPUtils.splitQuery(requestURI.getQuery());
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            if (entry.getValue().size() > 1) {
                throw new InvalidRequestException(HttpURLConnection.HTTP_BAD_REQUEST, "multiple parameters are not allowed");
            }

            if (UserSearchParams.URL_SEARCH_PARAM_NAME_LOGIN.equals(entry.getKey())) {
                builder.login(entry.getValue().get(0));
            }

            if (UserSearchParams.URL_SEARCH_PARAM_NAME_FIRSTNAME.equals(entry.getKey())) {
                builder.firstName(entry.getValue().get(0));
            }

            if (UserSearchParams.URL_SEARCH_PARAM_NAME_LASTNAME.equals(entry.getKey())) {
                builder.lastName(entry.getValue().get(0));
            }

        }


        List<User> users = ctx.getUSER_SERVICE().getUsers(builder.build());
        List<UiUser> result = new ArrayList<>();
        for (User user : users) {
            UiUser uiUser = UiUser.builder().login(user.getLogin()).firstName(user.getFirstName()).lastName(user.getLastName()).build();
            result.add(uiUser);
        }

        return new ResponseEntity<>(result,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }


}
