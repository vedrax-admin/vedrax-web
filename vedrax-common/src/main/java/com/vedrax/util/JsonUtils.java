package com.vedrax.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static String toJson(Object object) {

        if (object == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json) {

        if (json == null) {
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<T>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(String json, Class<T> type) {

        if (json == null) {
            return null;
        }

        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public static <T> String listToJson(List<T> values) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<T>>() {
        }.getType();
        return gson.toJson(values, type);
    }

    public static <T> List<T> jsonToList(String json) {
        if (json == null) {
            return Collections.emptyList();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<T>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static String mapToJson(Map<String, String> params) {
        if (CollectionUtils.isEmpty(params)) {
            return "{}";
        }

        Gson gson = new Gson();
        return gson.toJson(params);
    }

    public static Map<String, String> jsonToMap(String json) {
        if (json == null) {
            return Collections.emptyMap();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        return gson.fromJson(json, type);

    }

}
