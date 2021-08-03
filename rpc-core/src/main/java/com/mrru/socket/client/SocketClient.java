package com.mrru.socket.client;

import com.mrru.RpcClient;
import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.enums.ResponseCode;
import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import com.mrru.serializer.CommonSerializer;
import com.mrru.socket.util.ObjectReader;
import com.mrru.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * Socket方式远程方法调用的消费者（客户端）
 *
 * @className: RpcClient
 * @author: 茹某
 * @date: 2021/8/1 10:10
 **/
public class SocketClient implements RpcClient
{
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final String host;
    private final int port;

    private CommonSerializer serializer;

    public SocketClient(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    /*
    直接使用Java的序列化方式，传输对象 ObjectOutputStream 和 ObjectInputStream
    通过Socket传输。创建一个Socket，获取ObjectOutputStream对象，
    然后把需要发送的对象传进去即可，接收时获取ObjectInputStream对象，readObject()方法就可以获得一个返回的对象。
     */
    public Object sendRequest(RpcRequest rpcRequest)
    {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        try (Socket socket = new Socket(host, port)) {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            //序列化对象，并且按传输协议，每个以 字节数组 写到输出流中去
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);

            //校验传输协议，并且反序列化 得到 返回值对象
            Object obj = ObjectReader.readObject(inputStream);

            //校验返回值对象
            RpcResponse rpcResponse = (RpcResponse) obj;
            if (rpcResponse == null){
                logger.error("服务调用失败, service:{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service: "+ rpcRequest.getInterfaceName());
            }
            if (rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                logger.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            return rpcResponse.getData();

        } catch (IOException  e) {
            logger.error("调用时有错误发生：", e);
            throw new RpcException("服务调用失败: ", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer)
    {
        this.serializer = serializer;
    }


}
