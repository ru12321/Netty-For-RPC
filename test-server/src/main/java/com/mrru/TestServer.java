package com.mrru;

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
        RpcServer rpcServer = new RpcServer();

        rpcServer.register(helloService,9000);

    }

}
