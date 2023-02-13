package com.pipiobjo.api;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import com.pipiobjo.app.Context;
import com.pipiobjo.domain.user.User;
import com.pipiobjo.domain.user.UserSearchParams;
import com.pipiobjo.webserver.Constants;
import com.pipiobjo.webserver.Endpoint;
import com.pipiobjo.webserver.EndpointRegistry;
import com.pipiobjo.webserver.RestContextPathHandler;
import com.pipiobjo.webserver.ResponseEntity;
import com.pipiobjo.webserver.StatusCode;
import com.pipiobjo.domain.user.UserService;
import com.pipiobjo.webserver.errors.InvalidRequestException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
public class APIHandler extends RestContextPathHandler {

    private UserService userService;
    private Context ctx;
    private String userAPIPath;

    EndpointRegistry registry = new EndpointRegistry();

    public APIHandler(Context ctx){
        this.loadContext(ctx);
    }
    @Override
    public void loadContext(Context ctx) {
        super.loadContextGeneric(ctx);
        this.ctx = ctx;
        this.userService = ctx.getUSER_SERVICE();

        String ctxPath = ctx.getConfig().getApp().getContextPath();
//        userAPIPath = ctxPath + "/user";

        UserCreationEndpoint userCreationEndpoint = new UserCreationEndpoint(this.ctx);
        registry.registerEndpoint(userCreationEndpoint);

        UserListEndpoint userListEndpoint = new UserListEndpoint(this.ctx);
        registry.registerEndpoint(userListEndpoint);

        GreetingEndpoint greetingEndpoint = new GreetingEndpoint(this.ctx);
        registry.registerEndpoint(greetingEndpoint);
    }


    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        byte[] response;

        if(isHttpGet(exchange)){
            ResponseEntity responseEntity = doGet(exchange);
            response = buildByteRespone(exchange, responseEntity);
        } else if (isHttpPost(exchange)) {
            ResponseEntity responseEntity = doPost(exchange);
            response = buildByteRespone(exchange, responseEntity);
        }else{
            throw new InvalidRequestException(HttpURLConnection.HTTP_BAD_REQUEST, "");
        }


//        if (path.startsWith(userAPIPath)) {
//            String userPath = path.replaceAll(userAPIPath, "");
//            if(isHttpGet(exchange)){
//                ResponseEntity e = getUsers(exchange.getRequestURI().getQuery());
//                response = buildByteRespone(exchange, e);
//            } else if ("POST".equals(exchange.getRequestMethod())) {
//                ResponseEntity e = doPost(exchange);
//                response = buildByteRespone(exchange, e);
//            } else {
//                throw ApplicationExceptions.methodNotAllowed(
//                        "Method " + exchange.getRequestMethod() + " is not allowed for " + exchange.getRequestURI()).get();
//            }
//        }else{
//            throw new InvalidRequestException(HttpURLConnection.HTTP_BAD_REQUEST, "");
//        }

        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    private ResponseEntity doGet(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        List<Endpoint> getEndpoints = registry.findEndpoint("GET", path);

        if(getEndpoints.size()>1){
            log.error("multiple endpoints found to POST for path= {}, endpoints: {}", path, getEndpoints);
            throw new InvalidRequestException(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        } else if (getEndpoints.size() <1) {
            log.error("no endpoints found to POST for path= {}", path);
            throw new InvalidRequestException(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        }else{
            Endpoint endpoint = getEndpoints.get(0);
            return endpoint.doGet(exchange.getRequestURI(), exchange.getPrincipal());


        }

    }

    private ResponseEntity doPost(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        List<Endpoint> postEndpoints = registry.findEndpoint("POST", path);
        if(postEndpoints.size()>1){
            log.error("multiple endpoints found to POST for path= {}, endpoints: {}", path, postEndpoints);
            throw new InvalidRequestException(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        } else if (postEndpoints.size() <1) {
            log.error("no endpoints found to POST for path= {}", path);
            throw new InvalidRequestException(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        }else{
            Endpoint endpoint = postEndpoints.get(0);
            Class postBodyClass = endpoint.getPostBodyClass();
            Object o = super.readRequest(exchange.getRequestBody(), postBodyClass);

            return endpoint.doPost(o, exchange.getRequestURI(), exchange.getPrincipal());


        }
    }

//    private ResponseEntity getUsers(String query) {
//        Map<String, List<String>> urlQueryParams = getURLQueryParams(query);
//        boolean isValid = validateQueryParams(urlQueryParams);
//        UserSearchParams searchParams = UserSearchParams.builder().build();
//        List<User> users = userService.getUsers(searchParams);
//        return new ResponseEntity<>(users, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
//    }
//
//    protected static Headers getHeaders(String key, String value) {
//        Headers headers = new Headers();
//        headers.set(key, value);
//        return headers;
//    }
//
//    private boolean validateQueryParams(Map<String, List<String>> urlQueryParams) {
//
//        if(urlQueryParams.isEmpty()){
//            return true;
//        }
//
//        for(Map.Entry<String, List<String>> entry : urlQueryParams.entrySet()){
//            List<String> paramsValue = entry.getValue();
//            if(paramsValue.size() >1){
//                log.info("user query contains param {} multiple times", entry.getKey());
//                return false;
//            }
//
//        }
//        return false;
//    }

//    private ResponseEntity<RegistrationResponse> doPost2(InputStream is) {
//        RegistrationRequest registerRequest = super.readRequest(is, RegistrationRequest.class);
//
//        NewUser user = NewUser.builder()
//            .login(registerRequest.getLogin())
//            .password(PasswordEncoder.encode(registerRequest.getPassword()))
//            .build();
//
//        String userId = userService.create(user);
//
//        RegistrationResponse response = new RegistrationResponse(userId);
//
//        return new ResponseEntity<>(response,
//            getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
//    }


}
