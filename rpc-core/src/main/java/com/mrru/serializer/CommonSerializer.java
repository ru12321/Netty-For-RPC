package com.mrru.serializer;

/**
 * 通用的序列化反序列化接口
 *
 * @className: CommonSerializer
 * @author: 茹某
 * @date: 2021/8/2 8:37
 **/
public interface CommonSerializer
{
    //序列化，
    byte[] serialize(Object obj);

    //反序列化，获得该序列化器的编号，已经根据编号获取序列化器
    Object deserialize(byte[] bytes, Class<?> clazz);

    //获得该序列化器的编号
    int getCode();

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    //已经根据编号获取 对应的序列化器（反序列化用到）
    static CommonSerializer getByCode(int code)
    {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            case 3:
                return new ProtoBufSerializer();
            default:
                return null;
        }
    }
}
