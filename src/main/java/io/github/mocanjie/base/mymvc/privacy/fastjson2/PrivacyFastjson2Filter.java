package io.github.mocanjie.base.mymvc.privacy.fastjson2;

import com.alibaba.fastjson2.filter.ValueFilter;
import io.github.mocanjie.base.mymvc.privacy.Privacy;
import io.github.mocanjie.base.mymvc.privacy.PrivacyMaskUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PrivacyFastjson2Filter implements ValueFilter {

    // key: 类 -> (字段名 -> @Privacy)，无注解字段存 null 占位
    private final ConcurrentHashMap<Class<?>, Map<String, Privacy>> cache = new ConcurrentHashMap<>();

    @Override
    public Object apply(Object bean, String fieldName, Object fieldValue) {
        if (!(fieldValue instanceof String str) || bean == null) {
            return fieldValue;
        }
        Privacy privacy = getPrivacy(bean.getClass(), fieldName);
        if (privacy == null) {
            return fieldValue;
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
