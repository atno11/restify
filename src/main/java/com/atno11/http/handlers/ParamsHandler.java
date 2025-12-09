package com.atno11.http.handlers;

import fi.iki.elonen.NanoHTTPD;

import java.util.Map;

public class ParamsHandler {

    public ParamsHandler(NanoHTTPD.IHTTPSession session, Map<String, String> params){
        if (session.getQueryParameterString() != null) {
            session.getParameters().forEach((key, value) -> params.put(key, value.getFirst()));
        }
    }
}
