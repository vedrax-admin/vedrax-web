package com.vedrax.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

}
