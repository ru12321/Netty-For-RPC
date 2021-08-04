package com.mrru;

import com.mrru.serializer.KryoSerializer;
import com.mrru.transport.socket.server.SocketServer;

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
        //获取实现类对象
        HelloService helloService = new HelloServiceImpl2();

        //初始化服务端，指定序列化器，启动监听
        SocketServer socketServer = new SocketServer("127.0.0.1", 9999);
        socketServer.setSerializer(new KryoSerializer());

        //注册实现类对象 和 接口名称
        socketServer.publishService(helloService, HelloService.class);

    }

}
