package com.atno11.handlers;

import com.atno11.Restify;
import com.atno11.annotations.Config;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConfigHandler<T> {
    private T instance;
    private final Class<T> clazz;
    private final String fileName;
    private final File configFile;

    public ConfigHandler(Class<T> as) {
        if (!as.isAnnotationPresent(Config.class)) {
            Restify.LOGGER.error("Cannot identify config settings: Missing @ConfigSettings annotation on {}", as.getName());
            throw new IllegalArgumentException("Missing @ConfigSettings annotation on " + as.getName());
        }
        clazz = as;
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();

            Config annotation = clazz.getAnnotation(Config.class);
            fileName = annotation.fileName();
            configFile = new File(getPath());
        } catch (NoSuchMethodException e) {
            Restify.LOGGER.error("Class {} must have a public no-argument constructor for configuration.", as.getName(), e);
            throw new RuntimeException("Configuration class " + as.getName() + " must have a public no-argument constructor.", e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Restify.LOGGER.error("Failed to create instance of config class {}.", as.getName(), e);
            throw new RuntimeException("Failed to instantiate configuration class " + as.getName(), e);
        }

    }

    public T getData(){
        return this.instance;
    }

    public String getPath(){
        return "config/mine-rest-api/" + fileName;
    }

    public void load(){
        if (!configFile.exists()) {
            Restify.LOGGER.warn("Config file {} not founded, creating...", getPath());
            save();
            return;
        }
        try (Reader reader = new FileReader(configFile)){
            instance = Restify.GSON.fromJson(reader, clazz);
            Restify.LOGGER.debug("Loaded config:\n{}", Restify.GSON.toJson(instance));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(){
        try {
            configFile.getParentFile().mkdirs();
            try (Writer writer = new FileWriter(configFile)) {
                Restify.GSON.toJson(instance, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
