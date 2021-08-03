package com.mrru.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.mrru.enums.SerializerCode;
import com.mrru.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 基于Hessian协议的序列化器
 * @className: HessianSerializer
 * @author: 茹某
 * @date: 2021/8/3 12:31
 **/
/*
Hessian序列化是一种支持动态类型、跨语言、基于对象传输的网络协议，Java对象序列化的二进制流可以被其他语言（如，c++，python）。特性如下：
    自描述序列化类型。不依赖外部描述文件或者接口定义，用一个字节表示常用的基础类型，极大缩短二进制流。
    语言无关，支持脚本语言
    协议简单，比Java原生序列化高效
    相比hessian1，hessian2中增加了压缩编码，其序列化二进制流大小事Java序列化的50%，序列化耗时是Java序列化的30%，反序列化耗时是Java序列化的20%。
    Hessian会把复杂的对象所有属性存储在一个Map中进行序列化。所以在父类、子类中存在同名成员变量的情况下，hessian序列化时，先序列化子类，然后序列化父类。因此，反序列化结果会导致子类同名成员变量被父类的值覆盖。

    hessian序列化的效率更高，且序列化的数据更小，在基于RPC的调用方式中性能更好。
 */
public class HessianSerializer implements CommonSerializer
{

    private static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

    @Override
    public byte[] serialize(Object obj)
    {
        HessianOutput hessianOutput = null;
        //创建一个字节数组缓冲区--字节数组输出流
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            //对象序列化后，读到这个 字节数组缓冲区 中
            hessianOutput.writeObject(obj);
            //返回序列化后的字节数组
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e)
        {
            logger.error("序列化时有错误发生： ", e);
            throw new SerializeException("序列化时有错误发生");
        }finally
        {
            if (hessianOutput != null){
                try
                {
                    hessianOutput.close();
                } catch (IOException e)
                {
                    logger.error("关闭序列化流时有错误发生：",e);
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz)
    {
        HessianInput hessianInput = null;
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes))
        {
            hessianInput = new HessianInput(byteArrayInputStream);
            //将输入流中的 字节数组 反序列化为对象
            return hessianInput.readObject();
        } catch (Exception e)
        {
            logger.error("序列化时有错误发生： ", e);
            throw new SerializeException("序列化时有错误发生");
        } finally
        {
            if (hessianInput !=null){
                hessianInput.close();
            }
        }
    }

    @Override
    public int getCode()
    {
        return SerializerCode.valueOf("HESSIAN").getCode();
    }
}
