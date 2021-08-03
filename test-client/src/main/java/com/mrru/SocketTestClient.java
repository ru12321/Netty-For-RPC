package com.mrru;

import com.mrru.socket.client.SocketClient;

/**
 * @className: TestClient
 * @author: 茹某
 * @date: 2021/8/1 11:10
 **/
public class SocketTestClient
{
    /*
    需要通过动态代理，生成代理对象，
    并且调用，动态代理会自动帮我们向服务端发送请求的：
     */
    public static void main(String[] args)
    {
        //生成代理对象
        SocketClient client = new SocketClient("127.0.0.1", 9000);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);

        //要发送的数据
        HelloObject object = new HelloObject(27, "八月一日 开始手撸rpc");

        //调用RpcClientProxy的invoke方法
        String result = helloService.hello(object);
        System.out.println(result);

    }
}
