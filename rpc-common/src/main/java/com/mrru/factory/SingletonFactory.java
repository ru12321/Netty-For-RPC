package com.mrru.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例工厂
 *
 * @className: SingletonFactory
 * @author: 茹某
 * @date: 2021/8/4 15:47
 **/
public class SingletonFactory
{

    private static Map<Class, Object> objectMap = new HashMap<>();

    private SingletonFactory()
    {
    }

    public static <T> T getInstance(Class<T> clazz)
    {
        Object instance = objectMap.get(clazz);
        synchronized (clazz) {
            if (instance == null) {
                try {
                    instance = clazz.newInstance();//newInstance实例化对象是只能调用无参构造方法
                    objectMap.put(clazz, instance);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return clazz.cast(instance);
    }


}
