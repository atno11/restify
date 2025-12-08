package com.atno11.configs;

import com.atno11.annotations.Config;

@Config(fileName = "api-settings.json")
public class ApiConfig {

    @SuppressWarnings("FieldCanBeLocal")
    private final boolean SSL = false;
    @SuppressWarnings("FieldCanBeLocal")
    private final String HOSTNAME = "localhost";
    @SuppressWarnings("FieldCanBeLocal")
    private final int PORT = 25580;
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean REQUIRE_AUTHENTICATION = false;
    @SuppressWarnings("FieldCanBeLocal")
    private final String AUTHENTICATION_KEY = "";
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean SWAGGER = true;

    public boolean getSsl(){
        return this.SSL;
    }

    public String getHostName(){
        return this.HOSTNAME;
    }

    public int getPort(){
        return this.PORT;
    }

    public boolean getRequireAuthentication(){
        return this.REQUIRE_AUTHENTICATION;
    }

    public String getAuthenticationKey(){
        return this.AUTHENTICATION_KEY;
    }

    public boolean getSwagger(){
        return this.SWAGGER;
    }
}
