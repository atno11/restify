package com.atno11.http.handlers;


import com.atno11.http.annotations.PathVariable;
import com.atno11.http.definitions.RouteDefinition;
import com.atno11.http.utils.ConversionUtils;
import com.atno11.http.utils.PathUtils;
import fi.iki.elonen.NanoHTTPD;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class RouteHandler {
    private final List<RouteDefinition> routes;

    public RouteHandler(List<RouteDefinition> routes) {
        this.routes = routes;
    }

    public NanoHTTPD.Response handleRouteMatching(String uri, NanoHTTPD.Method method) {
        uri = PathUtils.normalizePath(uri);
        for (RouteDefinition route : this.routes) {
            // Jump to next item if route http method is not equal of request http method.
            if (!route.getHttpMethod().equals(method)) continue;
            // Jump to next item if uri path variables don't match route path variables;
            Map<String, String> pathVariables = PathUtils.extractPathVariables(route.getFullPath(), uri);
            if (pathVariables == null) continue;
            try {
                Method routeMethod = route.getMethod();
                Parameter[] routeParams = routeMethod.getParameters();
                Object[] routeArgs = new Object[routeParams.length];

                for (int i = 0; i < routeParams.length; i++) {
                    Parameter routeParam = routeParams[i];
                    if (routeParam.isAnnotationPresent(PathVariable.class)){
                        PathVariable pathVariable = routeParam.getAnnotation(PathVariable.class);
                        String name;
                        if (pathVariable.value() == null || pathVariable.value().isEmpty())
                            name = routeParam.getName();
                        else
                            name = pathVariable.value();

                        String value = pathVariables.get(name);
                        Object routeArg = ConversionUtils.convertStringToType(value, routeParam.getType(), routeParam.getParameterizedType());
                        routeArgs[i] = routeArg;
                    }
                }

                NanoHTTPD.Response result = (NanoHTTPD.Response) routeMethod.invoke(route.getControllerInstance(), routeArgs);
                return result;

            } catch (Exception e) {
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "Internal server error\n" + e.getMessage());
            }

        }
        // General router error response;
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "Route not found");
    }


}
