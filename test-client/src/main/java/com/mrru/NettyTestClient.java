package com.mrru;

import com.mrru.loadbalancer.RandomLoadBalancer;
import com.mrru.serializer.CommonSerializer;
import com.mrru.transport.RpcClient;
import com.mrru.transport.RpcClientProxy;
import com.mrru.transport.netty.client.NettyClient;

/**
 * 测试用Netty消费者
 *
 * @className: NettyTestClient
 * @author: 茹某
 * @date: 2021/8/2 10:05
 **/
public class NettyTestClient
{

    public static void main(String[] args)
    {
        //初始化客户端，手动传入序列化器(默认去RpcClient查看)，并传入proxy对象中
        //客户端不需要指定 服务端地址
        RpcClient  client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER, new RandomLoadBalancer());
        RpcClientProxy proxy = new RpcClientProxy(client);

        //生成代理对象
        HelloService helloService = proxy.getProxy(HelloService.class);

        //要发送的数据
        HelloObject object = new HelloObject(84, "8.2号学Rpc");

        //调用invoke，将要调用的方法 封装在RpcRequest对象中
        //连接服务器，并调用客户端 发送数据,后经过 CommonEncoder、CommonDecoder 传到Socket连接中
        //阻塞获得返回结果
        String result = helloService.hello(object);

        System.out.println(result);

    }

}
