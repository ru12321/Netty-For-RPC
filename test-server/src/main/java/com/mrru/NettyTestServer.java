package com.mrru;

import com.mrru.transport.netty.server.NettyServer;
import com.mrru.registry.ServiceProviderImpl;
import com.mrru.registry.ServiceProvider;
import com.mrru.serializer.JsonSerializer;

/**
 * 测试用Netty服务提供者（服务端）
 *
 * @className: NettyTestServer
 * @author: 茹某
 * @date: 2021/8/2 10:04
 **/
public class NettyTestServer
{
    public static void main(String[] args)
    {
        //创建实现类对象
        HelloServiceImpl helloService = new HelloServiceImpl();

        //启动服务，并监听端口9999的客户端连接
        NettyServer nettyServer = new NettyServer("127.0.0.1", 8484);
        nettyServer.setSerializer(new JsonSerializer());

        //传入服务端的地址，注册实现类对象，即对应 接口名称<-->实现类对象
        nettyServer.publishService(helloService,HelloService.class);
    }

}
