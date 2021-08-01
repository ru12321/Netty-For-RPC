package com.mrru.server;

import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @className: WorkerThread
 * @author: 茹某
 * @date: 2021/8/1 10:55
 **/
public class WorkerThread implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(WorkerThread.class);

    //和客户端连接的socket
    private Socket socket;

    //真正的实现类
    private Object service;

    public WorkerThread(Socket socket, Object service)
    {
        this.socket = socket;
        this.service = service;
    }

    /*
        WorkerThread实现了Runnable接口，用于接收RpcRequest对象，
        解析并且调用，生成RpcResponse对象并传输回去。
         */
    @Override
    public void run()
    {
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())){

            //输入流中 读到客户端包装的 rpcRequest对象,里面的属性parameters 是要传递的HelloObject对象
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();

            //通过反射 获得指定方法 拆开 rpcRequest对象中的属性
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            //通过反射调用这个方法，方法.（对象，“方法的参数”），调用service这个对象的这个方法method 参数设置为rpcRequest.getParameters()
            Object returnObject = method.invoke(service, rpcRequest.getParameters());

            objectOutputStream.writeObject(RpcResponse.success((returnObject)));
            objectOutputStream.flush();

        }catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            logger.error("调用或者发送时有错误发生： ",e);
        }

    }
}
