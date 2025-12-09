package com.atno11.http.handlers;

import com.atno11.Restify;
import fi.iki.elonen.NanoHTTPD;

public class AuthenticationHandler {

    public AuthenticationHandler(final String authKey){
        this.authKey = authKey;
    }
    private final String authKey;

    public NanoHTTPD.Response handleAuthentication(NanoHTTPD.IHTTPSession session, String uri){
        String authHeader = session.getHeaders().get("authorization");
        if (authHeader == null || !authHeader.equals(this.authKey)) {
            Restify.LOGGER.warn("Unauthorized request for: {}", uri);
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.UNAUTHORIZED, NanoHTTPD.MIME_PLAINTEXT, "Unauthorized");
        }
        return null;
    }
}
