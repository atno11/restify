package com.atno11.http.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathUtils {
    public static class Compiled {
        public final Pattern pattern;
        public final List<String> paramNames;
        public Compiled(Pattern pattern, List<String> paramNames) {
            this.pattern = pattern;
            this.paramNames = paramNames;
        }
    }

    public static Map<String, String> extractPathVariables(String routePath, String requestPath){
        String[] routeParts = routePath.split("/");
        String[] reqParts = requestPath.split("/");

        if (routeParts.length != reqParts.length) return null;

        Map<String, String> pathVariables = new HashMap<>();
        for (int i = 0; i < routeParts.length; i++) {
            String rp = routeParts[i];
            String rq = reqParts[i];

            if (rp.startsWith("{") && rp.endsWith("}")) {
                String varName = rp.substring(1, rp.length() - 1);
                pathVariables.put(varName, rq);
            } else if (!rp.equals(rq)) {
                return null; // Não casa
            }
        }

        return pathVariables;
    }

    public static String extractRawPath(String uri){
        return uri;
    }

    public static String normalizePath(String path) {
        if (path == null || path.isEmpty()) return "/";

        path = path.trim().replaceAll("/+", "/");

        // Remove barra final, exceto se for "/"
        if (path.endsWith("/") && path.length() > 1) path = path.substring(0, path.length() - 1);

        return path;
    }

    public static String join(String... paths) {
        if (paths == null || paths.length == 0) return "/";

        StringBuilder sb = new StringBuilder();

        for (String p : paths) {
            if (p == null || p.isEmpty()) continue;

            p = normalizePath(p);

            if (sb.length() == 0) {
                sb.append(p); // primeiro path
            } else {
                if (!sb.toString().endsWith("/")) sb.append("/");
                if (p.startsWith("/")) p = p.substring(1);
                sb.append(p);
            }
        }

        return normalizePath(sb.toString());
    }

    public static Compiled compile(String rawPath) {
        StringBuilder regex = new StringBuilder();
        List<String> names = new ArrayList<>();
        regex.append("^");
        int i = 0;
        while (i < rawPath.length()) {
            char c = rawPath.charAt(i);
            if (c == '{') {
                int end = rawPath.indexOf('}', i);
                if (end == -1) throw new IllegalArgumentException("Unclosed { in path: " + rawPath);
                String name = rawPath.substring(i + 1, end);
                names.add(name);
                regex.append("([^/]+)");
                i = end + 1;
            } else {
                if ("\\.[]{}()+*?^$|".indexOf(c) >= 0) regex.append("\\");
                regex.append(c);
                i++;
            }
        }
        regex.append("$");
        return new Compiled(Pattern.compile(regex.toString()), names);
    }

    public static Map<String,String> extract(Matcher matcher, List<String> names) {
        Map<String,String> map = new HashMap<>();
        for (int i = 0; i < names.size(); i++) {
            String val = matcher.group(i+1);
            map.put(names.get(i), val);
        }
        return map;
    }
}
