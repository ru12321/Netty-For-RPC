package com.mrru;

import com.mrru.entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RPC客户端动态代理，生成代理对象并且实现 invoke包装要发送的数据
 *
 * @className: RpcClientProxy
 * @author: 茹某
 * @date: 2021/8/1 10:04
 **/
public class RpcClientProxy implements InvocationHandler
{
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RpcClient client;

    public RpcClientProxy(RpcClient client)
    {
        this.client = client;
    }

    //使用getProxy()方法来生成代理对象。
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz)
    {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    //指明代理对象的方法被调用时的动作
    //显然就需要生成一个RpcRequest对象，发送出去，然后返回从服务端接收到的结果即可：
    //args 是 要传递的 HelloObject 对象
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());

        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(), method.getName(), args, method.getParameterTypes());

        return client.sendRequest(rpcRequest);
    }
}
