package com.mrru.client;

import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @className: RpcClientProxy
 * @author: 茹某
 * @date: 2021/8/1 10:04
 **/
public class RpcClientProxy implements InvocationHandler
{
    private String host;

    private int  port;

    public RpcClientProxy(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    //使用getProxy()方法来生成代理对象。
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                                 new Class<?>[] {clazz},
                                this);
    }

    //指明代理对象的方法被调用时的动作
    //显然就需要生成一个RpcRequest对象，发送出去，然后返回从服务端接收到的结果即可：
    //args 是 要传递的 HelloObject 对象
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        RpcClient rpcClient = new RpcClient();
        return ((RpcResponse) rpcClient.sendRequest(rpcRequest, host, port)).getData();


    }
}
