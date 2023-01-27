package com.pipiobjo.app;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

public class MyBasicAuthenticator extends BasicAuthenticator {
    private final Context ctx;

    public MyBasicAuthenticator(String realm, Context ctx) {
        super(realm);
        this.ctx = ctx;
    }


    public Result authenticate (HttpExchange t)
    {
        String requestPath = t.getRequestURI().getPath();
        if(requestPath.contains("/api/greeting")){
            return super.authenticate(t);
        }
            return new Authenticator.Success (
                    new HttpPrincipal(
                            "guest", realm
                    )
            );
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        boolean login = ctx.getUSER_SERVICE().login(username, password);
        return login;
    }
}
