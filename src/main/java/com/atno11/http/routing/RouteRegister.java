package com.atno11.http.routing;


import com.atno11.Restify;
import com.atno11.http.annotations.RequestMapping;
import com.atno11.http.annotations.RestController;
import com.atno11.http.annotations.methods.*;
import com.atno11.http.definitions.RouteDefinition;
import com.atno11.http.utils.PathUtils;
import fi.iki.elonen.NanoHTTPD;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteRegister {

    public static List<RouteDefinition> scanAndRegister(List<RouteDefinition> routes, String ...packages){
        if (packages == null || packages.length == 0) {
            throw new IllegalArgumentException("Você deve fornecer ao menos um package para scan.");
        }

        Map<Class<?>, Object> instanceByType = new HashMap<>();
        List<Class<?>> discoveredClasses = new ArrayList<>();

        try (
            ScanResult scanResult = new ClassGraph()
                    .enableAllInfo()
                    .acceptPackages(packages)
                    .scan();
            ){
            /** Rest Controllers */
            handleRestControllers(routes, scanResult.getClassesWithAnnotation(RestController.class.getName()));
        } catch (Exception e) {
            throw new RuntimeException("An error occurred at packages scan of routes register\n", e);
        }

        return routes;
    }

    private static void handleRestControllers(List<RouteDefinition> routes, ClassInfoList restControllers){
        for (ClassInfo ci : restControllers) {
            try {
                Class<?> cls = ci.loadClass();
                Object instance = null;
                try {
                    instance = cls.getDeclaredConstructor().newInstance();
                } catch (NoSuchMethodException ignored) { }

                String basePath = getRequestMappingBasePath(cls);

                for (Method method : cls.getDeclaredMethods()) {
                    NanoHTTPD.Method httpMethod = getHttpMethod(method);

                    if (httpMethod == null) continue;

                    String rawPath = getRawPath(method);

                    String fullPath = PathUtils.join(basePath, rawPath);

                    routes.add(new RouteDefinition(httpMethod, fullPath, instance, method));
                }

            } catch (Throwable t) {
                t.printStackTrace(); // não interrompe scan
            }
        }

    }

    private static String getRequestMappingBasePath(Class<?> cls){
        if (cls.isAnnotationPresent(RequestMapping.class)){
            return cls.getAnnotation(RequestMapping.class).value();
        }
        return "/";
    }

    private static NanoHTTPD.Method getHttpMethod(Method method){
        if (method.isAnnotationPresent(Get.class)) return NanoHTTPD.Method.GET;
        else if (method.isAnnotationPresent(Post.class)) return NanoHTTPD.Method.POST;
        else if (method.isAnnotationPresent(Put.class)) return NanoHTTPD.Method.PUT;
        else if (method.isAnnotationPresent(Patch.class)) return NanoHTTPD.Method.PATCH;
        else if (method.isAnnotationPresent(Delete.class)) return NanoHTTPD.Method.DELETE;
        return null;
    }

    private static String getRawPath(Method method){
        if (method.isAnnotationPresent(Get.class)) return method.getAnnotation(Get.class).value();
        else if (method.isAnnotationPresent(Post.class)) return method.getAnnotation(Post.class).value();
        else if (method.isAnnotationPresent(Put.class)) return method.getAnnotation(Put.class).value();
        else if (method.isAnnotationPresent(Patch.class)) return method.getAnnotation(Patch.class).value();
        else if (method.isAnnotationPresent(Delete.class)) return method.getAnnotation(Delete.class).value();
        return null;
    };


}
