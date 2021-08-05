package com.mrru;

import com.mrru.annotation.ServiceScan;
import com.mrru.serializer.CommonSerializer;
import com.mrru.transport.RpcServer;
import com.mrru.transport.socket.server.SocketServer;

/**
 * 测试用服务提供方（服务端）
 *
 * @className: TestServer
 * @author: 茹某
 * @date: 2021/8/1 11:10
 **/
@ServiceScan
public class SocketTestServer
{
    public static void main(String[] args)
    {
        //自动注册！
        RpcServer server = new SocketServer("127.0.0.1", 9998, CommonSerializer.HESSIAN_SERIALIZER);
        server.start();
    }

}
