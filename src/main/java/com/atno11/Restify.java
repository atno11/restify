package com.atno11;

import com.atno11.configs.ApiConfig;
import com.atno11.handlers.ConfigHandler;
import com.atno11.http.HttpServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.iki.elonen.NanoHTTPD;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Restify implements ModInitializer {
	public static final String MOD_ID = "Restify";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static MinecraftServer SERVER;
    public static ConfigHandler<ApiConfig> apiConfig = new ConfigHandler<>(ApiConfig.class);
    public static HttpServer HTTP_SERVER;
    private static String GLOBAL_PREFIX = "/api/v1";

    public static void setGlobalPrefix(String globalPrefix){
        if (globalPrefix == null || globalPrefix.isEmpty())
            throw new IllegalArgumentException("globalPrefix cannot be null or empty.");

        String prefix = globalPrefix;
        if (!globalPrefix.startsWith("/")) prefix = "/" + globalPrefix;
        if (!globalPrefix.endsWith("/")) prefix = globalPrefix + "/";

        GLOBAL_PREFIX = globalPrefix;
    }

    public static String getGlobalPrefix(){
        return GLOBAL_PREFIX;
    }

	@Override
	public void onInitialize() {
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
        LOGGER.warn("Client environment detected — skipping mod initialization.");
        return;
    }
    // Load api-settings config file;
    apiConfig.load();
    // Handle HTTP server initialization at minecraft server start;
    ServerLifecycleEvents.SERVER_STARTED.register(server -> {
        SERVER = server;
        LOGGER.info("Fabric server instance initialized successfully");
        ApiConfig api = apiConfig.getData();
        try {
            setGlobalPrefix(api.getGlobalPrefix());

            HTTP_SERVER = new HttpServer(api.getHostName(), api.getPort(), api.getRequireAuthentication(), api.getAuthenticationKey());

            HTTP_SERVER.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);

            LOGGER.info("HTTP server initialized successfully at port: {}", api.getPort());
        } catch (IOException e) {
            LOGGER.info("A Failure occurred at HTTP server initialization: {}", e.getMessage());
            e.printStackTrace();
        }
    });
    // Handle HTTP server stop while minecraft server is stopping.;
    ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
        if (HTTP_SERVER != null){
            HTTP_SERVER.stop();
        }
    });

	}
}