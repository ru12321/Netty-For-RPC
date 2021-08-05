package com.mrru;

import com.mrru.annotation.ServiceScan;
import com.mrru.serializer.CommonSerializer;
import com.mrru.transport.netty.server.NettyServer;

/**
 * 测试用Netty服务提供者（服务端）
 *
 * @className: NettyTestServer
 * @author: 茹某
 * @date: 2021/8/2 10:04
 **/
@ServiceScan
public class NettyTestServer
{
    public static void main(String[] args)
    {
        //启动服务，并监听端口9999的客户端连接, 手动传入序列化器(默认去RpcServer查看)
        NettyServer nettyServer = new NettyServer("127.0.0.1", 8888, CommonSerializer.PROTOBUF_SERIALIZER);

        //自动注册此根目录包下的 标有 @Service的类并进行注册
        nettyServer.start();
    }

}
