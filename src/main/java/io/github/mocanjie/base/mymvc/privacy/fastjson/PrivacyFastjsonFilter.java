package io.github.mocanjie.base.mymvc.privacy.fastjson;

import com.alibaba.fastjson.serializer.ValueFilter;
import io.github.mocanjie.base.mymvc.privacy.Privacy;
import io.github.mocanjie.base.mymvc.privacy.PrivacyMaskUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PrivacyFastjsonFilter implements ValueFilter {

    private final ConcurrentHashMap<Class<?>, Map<String, Privacy>> cache = new ConcurrentHashMap<>();

    @Override
    public Object process(Object object, String name, Object value) {
        if (!(value instanceof String str) || object == null) {
            return value;
        }
        Privacy privacy = getPrivacy(object.getClass(), name);
        if (privacy == null) {
            return value;
        }
        return PrivacyMaskUtils.mask(str, privacy);
    }

    private Privacy getPrivacy(Class<?> clazz, String fieldName) {
        Map<String, Privacy> fieldMap = cache.computeIfAbsent(clazz, this::scanPrivacyFields);
        return fieldMap.get(fieldName);
    }

    private Map<String, Privacy> scanPrivacyFields(Class<?> clazz) {
        Map<String, Privacy> map = new ConcurrentHashMap<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                Privacy privacy = field.getAnnotation(Privacy.class);
                if (privacy != null) {
                    map.putIfAbsent(field.getName(), privacy);
                }
            }
            current = current.getSuperclass();
        }
        return map;
    }
}
