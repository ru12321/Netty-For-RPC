package com.mrru.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示一个服务提供类，用于远程接口的实现类; @Service 放在一个类上，标识这个类提供一个服务
 *
 * @author: 茹某
 * @date: 2021/8/5 16:22
 **/
@Target(ElementType.TYPE)           //元注解：用在类上
@Retention(RetentionPolicy.RUNTIME) //元注解：注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在
public @interface Service
{
    //该服务的名称，默认值是该类的完整类名
    //参数类型String + 参数名name（）；
    public String name() default "";

}