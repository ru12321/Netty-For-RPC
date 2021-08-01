package com.mrru;

import com.mrru.registry.DefaultServiceRegistry;
import com.mrru.registry.ServiceRegistry;
import com.mrru.server.RpcServer;

/**
 * @className: TestServer
 * @author: 茹某
 * @date: 2021/8/1 11:10
 **/
public class TestServer
{
    /*
    我们只需要创建一个RpcServer并且把这个实现类注册进去就行了：
     */
    public static void main(String[] args)
    {
        HelloServiceImpl helloService = new HelloServiceImpl();

        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);

        RpcServer rpcServer = new RpcServer(serviceRegistry);

        rpcServer.start(9000);

    }

}
