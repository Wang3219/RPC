package github.Wang3219.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例工厂类
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-07 16:16
 * @Description:
 */
public class SingletonFactory {
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    public static <T> T getInstance(Class<T> clazz) {
        if (clazz == null)
            throw new IllegalArgumentException();
        String key = clazz.toString();
        // 若存在则直接返回，不存在则创建
        if (OBJECT_MAP.containsKey(key))
            return clazz.cast(OBJECT_MAP.get(key));
        else {
            return clazz.cast(OBJECT_MAP.computeIfAbsent(key, k -> {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException();
                }
            }));
        }
    }
}
