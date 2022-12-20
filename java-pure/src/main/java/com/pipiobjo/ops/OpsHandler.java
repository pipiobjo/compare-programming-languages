package com.pipiobjo.ops;

import com.pipiobjo.app.Context;
import com.pipiobjo.ops.probes.LiveResponse;
import com.pipiobjo.ops.probes.ReadyResponse;
import com.pipiobjo.ops.probes.StartupResponse;
import com.pipiobjo.webserver.Constants;
import com.pipiobjo.webserver.ResponseEntity;
import com.pipiobjo.webserver.RestContextPathHandler;
import com.pipiobjo.webserver.StatusCode;
import com.pipiobjo.webserver.errors.ApplicationExceptions;
import com.pipiobjo.webserver.errors.InvalidRequestException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;

@Slf4j
public class OpsHandler extends RestContextPathHandler {

    private Context ctx;
    private String livePath;
    private String readyPath;
    private String startPath;

    public OpsHandler(Context ctx) {
        this.loadContext(ctx);
    }

    @Override
    public void loadContext(Context ctx) {
        super.loadContextGeneric(ctx);
        this.ctx = ctx;

        String ctxPath = ctx.getConfig().getOps().getContextPath();
        readyPath = ctxPath + "/ready";
        startPath = ctxPath + "/start";
        livePath = ctxPath + "/live";

    }

    @Override
    protected void execute(HttpExchange exchange) throws Exception {
        String path = exchange.getRequestURI().getPath();


        byte[] response;
        //ready probe
        if (readyPath.equals(path)) {
            if (isHttpGet(exchange)) {
                ResponseEntity<ReadyResponse> entity = buildReadyResponse();
                response = buildByteRespone(exchange, entity);
            } else {
                throw ApplicationExceptions.methodNotAllowed("Method " + exchange.getRequestMethod() + " is not allowed for " + exchange.getRequestURI()).get();
            }
            // start probe
        } else if (startPath.equals(path)) {
            if (isHttpGet(exchange)) {
                ResponseEntity<StartupResponse> entity = buildStartupResponse();
                response = buildByteRespone(exchange, entity);
            } else {
                throw ApplicationExceptions.methodNotAllowed("Method " + exchange.getRequestMethod() + " is not allowed for " + exchange.getRequestURI()).get();
            }
            // live probe
        } else if (livePath.equals(path)) {
            if (isHttpGet(exchange)) {
                ResponseEntity<LiveResponse> entity = buildLiveResponse();
                response = buildByteRespone(exchange, entity);
            } else {
                throw ApplicationExceptions.methodNotAllowed("Method " + exchange.getRequestMethod() + " is not allowed for " + exchange.getRequestURI()).get();
            }
        } else {
            throw new InvalidRequestException(400, "");
        }
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.flush();
        os.close();

    }
    protected static Headers getHeaders(String key, String value) {
        Headers headers = new Headers();
        headers.set(key, value);
        return headers;
    }

    private ResponseEntity<ReadyResponse> buildReadyResponse() {
        ReadyResponse resp = new ReadyResponse("ok");
        log.info("ready ok!");
        return new ResponseEntity<>(resp, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }

    private ResponseEntity<LiveResponse> buildLiveResponse() {
        LiveResponse resp = new LiveResponse("ok");

        return new ResponseEntity<>(resp, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }

    private ResponseEntity<StartupResponse> buildStartupResponse() {
        StartupResponse resp = new StartupResponse("ok");

        return new ResponseEntity<>(resp, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }

}
