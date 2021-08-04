package com.mrru.transport.socket.server;

import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.registry.local.ServiceProvider;
import com.mrru.RequestHandler;
import com.mrru.serializer.CommonSerializer;
import com.mrru.transport.socket.util.ObjectReader;
import com.mrru.transport.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * 处理RpcRequest的工作线程
 * @className: RequestHandlerThread
 * @author: 茹某
 * @date: 2021/8/1 18:34
 **/
public class SocketRequestHandlerThread implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(SocketRequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;

    public SocketRequestHandlerThread(Socket socket, RequestHandler requestHandler, CommonSerializer serializer)
    {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    @Override
    public void run()
    {
        try(InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream()) {

            //从输入流中读到 发送对象
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);

            //从本地注册中心 获取到 实现类对象
            //调用服务提供端的实现类的 方法，拿到方法的返回值
            //通过实现类对象service 调用包装对象中的具体方法-----handle是真正的代理实现服务端的方法，并返回 方法的返回值
            Object result = requestHandler.handle(rpcRequest);

            //将方法返回值封装为响应对象 并将响应对象 写到输出流(带上请求响应号)
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);

        } catch (IOException  e)
        {
            logger.error("调用或者发送时有错误发生： ",e);
        }
    }


}
