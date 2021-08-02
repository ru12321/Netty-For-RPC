package com.mrru.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.enums.SerializerCode;
import com.mrru.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @className: KryoSerializer
 * @author: 茹某
 * @date: 2021/8/2 15:25
 **/
public class KryoSerializer implements CommonSerializer
{

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);

        return kryo;
    });

    @Override
    public byte[] serialize(Object obj)
    {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)){
            //一个线程一个 Kryo
            Kryo kryo = kryoThreadLocal.get();

            //使用 writeObject 方法将对象写入Output中
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();

            //获得对象的字节数组
            return  output.toBytes();

        } catch (IOException e)
        {
            logger.error("序列化时有错误发生：", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz)
    {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input =new Input(byteArrayInputStream)){

            Kryo kryo = kryoThreadLocal.get();
            //从 Input 对象中直接 readObject
            //只需要传入对象的类型，而不需要具体传入每一个属性的类型信息。比JSON序列化方便
            Object object = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return object;

        } catch (IOException e)
        {
            logger.error("反序列化时有错误发生：", e);
            throw new SerializeException("反序列化时有错误发生");
        }

    }


    @Override
    public int getCode()
    {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
