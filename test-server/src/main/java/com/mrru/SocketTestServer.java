package com.mrru;

import com.mrru.registry.DefaultServiceRegistry;
import com.mrru.registry.ServiceRegistry;
import com.mrru.socket.server.SocketServer;

/**
 * 测试用服务提供方（服务端）
 *
 * @className: TestServer
 * @author: 茹某
 * @date: 2021/8/1 11:10
 **/
public class SocketTestServer
{
    /*
    我们只需要创建一个RpcServer并且把这个实现类注册进去就行了：
     */
    public static void main(String[] args)
    {
        HelloServiceImpl helloService = new HelloServiceImpl();

        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);

        SocketServer rpcServer = new SocketServer(serviceRegistry);

        rpcServer.start(9000);

    }

}
