package com.atno11.http;

import com.atno11.Restify;
import com.atno11.http.handlers.AuthenticationHandler;
import com.atno11.http.handlers.ParamsHandler;
import com.atno11.http.handlers.PathHandler;
import com.atno11.http.handlers.RouteHandler;
import com.atno11.http.definitions.RouteDefinition;
import com.atno11.http.routing.RouteRegister;
import fi.iki.elonen.NanoHTTPD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer extends NanoHTTPD {
    public HttpServer(String hostname, int port, boolean isAuthenticated, String authenticationKey) {
        super(hostname, port);
        this.isAuthenticated = isAuthenticated;
        this.authenticationHandler = new AuthenticationHandler(authenticationKey);
        this.routeHandler = new RouteHandler(this.routes);
        RouteRegister.scanAndRegister(this.routes,"com.atno11.endpoints.v1");
        Restify.LOGGER.info("HTTP Server was constructed successfully.");
    }
    private final boolean isAuthenticated;
    private final List<RouteDefinition> routes = new ArrayList<>();
    private final AuthenticationHandler authenticationHandler;
    private final RouteHandler routeHandler;

    @Override
    public Response serve(IHTTPSession session){

        String uri = session.getUri();
        NanoHTTPD.Method method = session.getMethod();

        return this.routeHandler.handleRouteMatching(uri, method);
    }

}
