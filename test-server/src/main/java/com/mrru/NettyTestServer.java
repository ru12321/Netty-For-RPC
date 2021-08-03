package com.mrru;

import com.mrru.netty.server.NettyServer;
import com.mrru.registry.DefaultServiceRegistry;
import com.mrru.registry.ServiceRegistry;

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
        //注册实现类对象，即对应 接口名称<-->实现类对象
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);

        //启动服务，并监听端口9999的客户端连接
        NettyServer server = new NettyServer();
        server.start(9999);
    }

}
