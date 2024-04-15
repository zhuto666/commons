package com.zhongqin.commons.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Json工具类
 *
 * @author Kevin
 * @date 2020/9/10 14:16
 */
public class JsonTools<T> {

    /**
     * Json字符串转对象
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * Json字符串转集合
     */
    public static <T> List<T> jsonToList(String json, Class<T> clazz) {
        return JSONArray.parseArray(json, clazz);
    }

    /**
     * 泛型转对象
     */
    public static <T> T jsonToObject(T data, Class object) {
        String listTxt = JSONArray.toJSONString(data);
        return (T) JSON.parseObject(listTxt, object);
    }

    /**
     * 泛型转集合
     */
    public static <T> T jsonToList(T data, Class object) {
        String listTxt = JSONArray.toJSONString(data);
        return (T) JSON.parseArray(listTxt, object);
    }

    /**
     * 对象转Json
     */
    public static String objectToJson(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * 判断字符串是否为Json格式
     *
     * @param content
     * @return
     */
    public static boolean isJson(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }
        boolean isJsonObject = true;
        boolean isJsonArray = true;
        try {
            JSONObject.parseObject(content);
        } catch (Exception e) {
            isJsonObject = false;
        }
        try {
            JSONObject.parseArray(content);
        } catch (Exception e) {
            isJsonArray = false;
        }
        return isJsonObject || isJsonArray;
    }
}
