package com.atno11.http.utils;

public class StringUtils {

    public static String join(String base, String path) {
        if (base == null) base = "";
        if (path == null) path = "";
        String combined = ("/" + base + "/" + path).replaceAll("/+", "/");
        if (!combined.startsWith("/")) combined = "/" + combined;
        return combined.equals("/") ? "/" : combined.replaceAll("/$", "");
    }

}
