package com.winjay.practice.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Gson解析
 *
 * @author Winjay
 * @date 2023-03-25
 */
public class GsonUtil {

    public static <T> T fromJson(String json, Class<T> c) {
        Gson gson = new Gson();
        T t = null;
        return t = gson.fromJson(json, c);
    }

    public static <T> T fromJson(Reader reader, Class<T> c) {
        Gson gson = new Gson();
        T t = null;
        return t = gson.fromJson(reader, c);
    }

    public static <T> List<T> fromListJson(String json, Class<T> c) {
        List<T> list = new ArrayList<>();
        Gson gson = new Gson();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement element : array) {
            list.add(gson.fromJson(element, c));
        }
        return list;
    }
}
