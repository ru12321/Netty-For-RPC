package com.mrru;

import com.mrru.serializer.CommonSerializer;
import com.mrru.transport.netty.server.NettyServer;
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
        HelloService helloService = new HelloServiceImpl();

        //启动服务，并监听端口9999的客户端连接, 手动传入序列化器(默认去RpcServer查看)
        NettyServer nettyServer = new NettyServer("127.0.0.1", 8888, CommonSerializer.PROTOBUF_SERIALIZER);

        //传入服务端的地址，注册实现类对象，即对应 接口名称<-->实现类对象
        nettyServer.publishService(helloService,HelloService.class);
    }

}
