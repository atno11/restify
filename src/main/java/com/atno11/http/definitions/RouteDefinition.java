package com.atno11.http.definitions;

import com.atno11.Restify;
import com.atno11.http.utils.PathUtils;
import fi.iki.elonen.NanoHTTPD;

import java.lang.reflect.Method;

public class RouteDefinition {
    private final NanoHTTPD.Method httpMethod;
    private final String fullPath;
    private final Object controllerInstance;
    private final Method method;

    public RouteDefinition(NanoHTTPD.Method httpMethod, String fullPath, Object controllerInstance, Method method) {
        this.httpMethod = httpMethod;
        this.fullPath = PathUtils.join(Restify.getGlobalPrefix(), fullPath);
        this.controllerInstance = controllerInstance;
        this.method = method;
        Restify.LOGGER.info("Registered {} {}", this.httpMethod, this.fullPath);
    }

    public NanoHTTPD.Method getHttpMethod() { return httpMethod; }
    public String getFullPath() { return fullPath; }
    public Object getControllerInstance() { return controllerInstance; }
    public Method getMethod() { return method; }
}
