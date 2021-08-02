package com.mrru.serializer;

/**
 * @className: CommonSerializer
 * @author: 茹某
 * @date: 2021/8/2 8:37
 **/
public interface CommonSerializer
{
    //序列化，
    byte[] serialize(Object obj);

    //反序列化，获得该序列化器的编号，已经根据编号获取序列化器
    Object deserialize(byte[] bytes,Class<?> clazz);

    //获得该序列化器的编号
    int getCode();

    //已经根据编号获取序列化器
    static CommonSerializer getByCode(int code){
        switch (code){
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }





}