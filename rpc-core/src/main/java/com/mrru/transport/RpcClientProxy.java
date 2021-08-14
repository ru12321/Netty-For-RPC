package com.mrru.transport;

import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.transport.netty.client.NettyClient;
import com.mrru.transport.socket.client.SocketClient;
import com.mrru.util.RpcMessageChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
    {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());

        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString()
                                                , method.getDeclaringClass().getName()
                                                , method.getName()
                                                , args
                                                , method.getParameterTypes()
                                                , false);

        RpcResponse rpcResponse = null;

        if (client instanceof NettyClient) {
            try {
                //当异步任务完成或者发生异常时，自动调用回调对象的回调方法。
                CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);
                rpcResponse = completableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("方法调用请求发送失败", e);
                return null;
            }
        }

        if (client instanceof SocketClient) {
            rpcResponse = (RpcResponse) client.sendRequest(rpcRequest);
        }

        //返回的对象 和 发送的对象 进行校验
        RpcMessageChecker.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }
}
