package com.mrru.socket.server;

import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.registry.ServiceRegistry;
import com.mrru.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 处理RpcRequest的工作线程
 * @className: RequestHandlerThread
 * @author: 茹某
 * @date: 2021/8/1 18:34
 **/
public class RequestHandlerThread implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry)
    {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run()
    {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())){
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();

            //通过接口名称 获取到 实现类对象
            Object service = serviceRegistry.getService(interfaceName);

            //通过实现类对象service 调用包装对象中的具体方法-----真正的代理实现服务端的方法，并返回 方法的返回值
            Object result = requestHandler.handle(rpcRequest, service);

            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();

        } catch (IOException | ClassNotFoundException e)
        {
            logger.error("调用或者发送时有错误发生： ",e);
        }
    }


}
