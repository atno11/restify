package com.atno11;

import com.atno11.configs.ApiConfig;
import com.atno11.handlers.ConfigHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Restify implements ModInitializer {
	public static final String MOD_ID = "restify";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static MinecraftServer SERVER;
    public static ConfigHandler<ApiConfig> apiConfig = new ConfigHandler<>(ApiConfig.class);
	@Override
	public void onInitialize() {
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
        LOGGER.warn("Client environment detected — skipping mod initialization.");
        return;
    }

    LOGGER.info("RESTIFY is Starting!");
    // Load api-settings config file;
    apiConfig.load();

    ServerLifecycleEvents.SERVER_STARTED.register(server -> {
        SERVER = server;
        LOGGER.info("Fabric server instance initialized successfully");
        ApiConfig api = apiConfig.getData();
    });
	}
}