package com.winjay.practice.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

/**
 * Json工具类
 * Created by Winjay on 2018/7/13.
 */
public final class JsonUtil {

    private static JsonUtil instance;

    public static final JsonUtil getInstance() {
        if (instance == null) {
            synchronized (JsonUtil.class) {
                if (instance == null) {
                    instance = new JsonUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 解析JSON
     *
     * @param json
     * @param trf
     * @return
     */
    public <T> T fromJson(String json, TypeReference<T> trf) {
        T t = null;
        try {
            t = JSON.parseObject(json, trf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 解析Json
     *
     * @param json
     * @param clzz
     * @return
     * @date 2015-5-26下午2:03:13
     */
    public <T> T fromJson(String json, Class<T> clzz) {
        T t = null;
        try {
            t = JSON.parseObject(json, clzz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 解析List<T>
     *
     * @param json
     * @param clzz
     * @param <T>
     * @return
     */
    public <T> List<T> fromJsonList(String json, Class<T> clzz) {
        return JSON.parseArray(json, clzz);
    }

    /**
     * 转换成JSON
     *
     * @param obj
     * @return
     */
    public String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }
}
