package com.atno11.http.handlers;

import java.util.List;

public class PathHandler {

    public static boolean isAllowedPath(String uri, boolean swagger){
        List<String> allowedPaths = swagger ? List.of(
                "/", "/swagger-ui-bundle.js", "/swagger-ui.css", "/api-docs", "/index.css",
                "/searchPlugin.js", "/swagger-ui-standalone-preset.js", "/swagger-initializer.js",
                "/favicon-32x32.png", "/swagger-ui.css.map", "/favicon-16x16.png"
        ) : List.of();
        return allowedPaths.stream().anyMatch(path -> uri.equals(path) || uri.startsWith("/swagger"));
    }
}
