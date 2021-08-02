package com.mrru.codec;

import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.enums.PackageType;
import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import com.mrru.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @className: CommonDecoder
 * @author: 茹某
 * @date: 2021/8/2 9:10
 **/
//将收到的字节序列 还原为 实际对象
public class CommonDecoder extends ReplayingDecoder
{
    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        int magic = in.readInt();
        if (magic != MAGIC_NUMBER){
            logger.error("不识别的协议包： {}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }

        int packageCode = in.readInt();
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        }else if(packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        }else{
            logger.error("不识别的数据包: {}",packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        int serializerCode = in.readInt();//目前值为1   取出序列化器的编号，以获得正确的反序列化方式
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);//如果是1，创建新的serializer，不然返回null
        if (serializer == null){
            logger.error("不识别的反序列化器：{}",serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }

        //并且读入 length 字段来确定数据包的长度（防止粘包）
        int length = in.readInt();
        byte[] bytes = new byte[length];

        //读入正确大小的字节数组，反序列化成对应的对象。
        in.readBytes(bytes);
        Object obj = serializer.deserialize(bytes, packageClass);// 解码后的 RpcRequest对象

        out.add(obj);
    }

}
