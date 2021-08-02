package com.mrru.codec;


import com.mrru.entity.RpcRequest;
import com.mrru.enums.PackageType;
import com.mrru.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @className: CommonEncoder
 * @author: 茹某
 * @date: 2021/8/1 21:40
 **/
/*
首先是 4 字节魔数，表识一个协议包。
Package Type,标明这是一个调用请求还是调用响应，
Serializer Type, 标明了实际数据使用的序列化器，这个服务端和客户端应当使用统一标准；
Data Length 就是实际数据的长度，设置这个字段主要防止粘包，最后就是经过序列化后的实际数据，
    可能是 RpcRequest 也可能是 RpcResponse 经过序列化后的字节，取决于 Package Type。
 */
public class CommonEncoder extends MessageToByteEncoder
{
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer){
        this.serializer = serializer;
    }

    //CommonEncoder 的工作很简单，
    // 就是把 RpcRequest 或者 RpcResponse 包装成协议包
    //根据上面提到的协议格式，将各个字段写到管道里就可以
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception
    {
        //魔数
        out.writeInt(MAGIC_NUMBER);

        //Package Type，标明这是一个调用请求还是调用响应
        if (msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());//0--请求
        }else{
            out.writeInt(PackageType.RESPONSE_PACK.getCode());//1--响应
        }

        //Serializer Type 标明了实际数据使用的序列化器
        out.writeInt(serializer.getCode());//这里恒定为1，编码器解码器，要使用一样的

        byte[] bytes = serializer.serialize(msg);
        //Data Length 就是实际数据的长度
        out.writeInt(bytes.length);

        //Data Bytes
        out.writeBytes(bytes);


    }





}
