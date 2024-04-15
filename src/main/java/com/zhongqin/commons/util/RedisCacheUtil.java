package com.zhongqin.commons.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Author：Ray
 * Date：2022/7/14 17:05
 * Version：1.0
 * description：
 *
 * @author Ray
 */
public class RedisCacheUtil<T> {

    private static StringRedisTemplate staticStringRedisTemplate;

    public RedisCacheUtil(StringRedisTemplate stringRedisTemplate) {
        staticStringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 缓存Redis数据
     */
    public static void set(String key, String value) {
        staticStringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存redis数据并给予失效时间
     */
    public static void set(String key, String value, long timeout, TimeUnit timeUnit) {
        staticStringRedisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 根据定义的key前缀 清空redis缓存
     */
    public static void deleteByKeyPrefix(@NonNull String key) {
        Set<String> redisKeySet = staticStringRedisTemplate.keys(key + "*");
        if (redisKeySet != null) {
            staticStringRedisTemplate.delete(redisKeySet);
        }
    }

    /**
     * 根据key删除redis缓存
     */
    public static void delete(@NonNull String key) {
        staticStringRedisTemplate.delete(key);
    }

    /**
     * 验证key是否存在
     */
    public static Boolean hasKey(String key) {
        return staticStringRedisTemplate.hasKey(key);
    }

    /**
     * 根据 key 获取 redis 返回 string
     */
    public static String get(@NonNull String key) {
        return staticStringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 根据 key 获取 redis 返回 泛型
     */
    public static <T> T get(@NonNull String key, @NonNull Class<T> clazz) {
        return JsonTools.jsonToObject(get(key), clazz);
    }

    /**
     * 根据 key 获取 redis 返回 list
     */
    public static <T> List<T> getRedisCacheToList(@NonNull String key, @NonNull Class<T> clazz) {
        return JsonTools.jsonToList(get(key), clazz);
    }

}
