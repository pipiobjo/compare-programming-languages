package com.pipiobjo.app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pipiobjo.api.APIHandler;
import com.pipiobjo.ops.OpsHandler;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {

        Configuration cfg = new Configuration();
        Context ctx = Context.builder().config(cfg).build();
        createApplicationHttpServer(ctx);
        createOperatingHttpServer(ctx);

    }

    private static void createOperatingHttpServer(Context ctx) throws IOException {
        int operating_http_port = ctx.getConfig().getOps().getPort();
        String operatingContextPath = ctx.getConfig().getOps().getContextPath();

        HttpServer server = HttpServer.create(new InetSocketAddress(operating_http_port), 0);

        OpsHandler opsHandler = new OpsHandler(ctx);
        server.createContext(operatingContextPath, opsHandler::handle);
        ExecutorService threadPool = Executors.newWorkStealingPool(3);
        server.setExecutor(threadPool);
        server.start();
        logger.info("{}:{} started",  operating_http_port, operatingContextPath);
    }

    private static void createApplicationHttpServer(Context ctx) throws IOException {
        int application_http_port = ctx.getConfig().getApp().getPort();
        String apiContextPath = ctx.getConfig().getApp().getContextPath();
        HttpServer server = HttpServer.create(new InetSocketAddress(application_http_port), 0);

        APIHandler APIHandler = new APIHandler(ctx);
        HttpContext context = server.createContext(apiContextPath, APIHandler::handle);
        context.setAuthenticator(new MyBasicAuthenticator("my-realm", ctx));
//        context.setAuthenticator(new BasicAuthenticator("myrealm") {
//            @Override
//            public boolean checkCredentials(String user, String pwd) {
//                return ctx.getUSER_SERVICE().login(user, pwd);
//            }
//        });

//        HttpContext context =server.createContext("/api/hello", (exchange -> {
//
//            if ("GET".equals(exchange.getRequestMethod())) {
//                Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
//                String noNameText = "Anonymous";
//                String name = params.getOrDefault("name", List.of(noNameText)).stream().findFirst().orElse(noNameText);
//                String respText = String.format("Hello %s!", name);
//                exchange.sendResponseHeaders(200, respText.getBytes().length);
//                OutputStream output = exchange.getResponseBody();
//                output.write(respText.getBytes());
//                output.flush();
//            } else {
//                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
//            }
//            exchange.close();
//        }));


        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor()); // creates a default executor
        server.start();
        logger.info("{}:{} started",  application_http_port, apiContextPath);
    }
}
