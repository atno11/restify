package com.atno11.endpoints.v1.controllers;

import com.atno11.Restify;
import com.atno11.http.annotations.PathVariable;
import com.atno11.http.annotations.RequestMapping;
import com.atno11.http.annotations.RestController;
import com.atno11.http.annotations.methods.Get;
import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/server")
public class ServerController {

    class User {
        public String name;
        public Long id;
        User(String name, Long id){ this.name = name; this.id = id; }
    }

    List<User> users = Arrays.asList(
            new User("Gilmar", 1L),
            new User("Jackson",2L)
    );

    @Get("/")
    public NanoHTTPD.Response hello(){
        return NanoHTTPD.newFixedLengthResponse("Hello World!");
    }

    @Get("users/")
    public NanoHTTPD.Response getUsers(){
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", Restify.GSON.toJson(users));
    }

    @Get("users/{id}")
    public NanoHTTPD.Response getUserById(@PathVariable("id") Long id){
        User user = users.stream().filter(u -> u.id != null && u.id.equals(id)).findFirst().orElse(null);
        if (user != null)
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", Restify.GSON.toJson(user));
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "User not found!");
    }

    @Get("users/{userId}/orders/{orderId}")
    public NanoHTTPD.Response getOrder(@PathVariable Long userId, @PathVariable Long orderId){
        return NanoHTTPD.newFixedLengthResponse("User " + userId + " Order " + orderId);
    }


}
