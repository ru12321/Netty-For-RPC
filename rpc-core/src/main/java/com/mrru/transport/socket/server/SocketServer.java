package com.mrru.transport.socket.server;

import com.mrru.RpcServer;
import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import com.mrru.registry.nacos.NacosServiceRegistry;
import com.mrru.registry.local.ServiceProvider;
import com.mrru.RequestHandler;
import com.mrru.registry.local.ServiceProviderImpl;
import com.mrru.registry.nacos.ServiceRegistry;
import com.mrru.serializer.CommonSerializer;
import com.mrru.factory.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Socket方式远程方法调用的提供者（服务端）
 *
 * @className: RpcServer
 * @author: 茹某
 * @date: 2021/8/1 10:45
 **/
public class SocketServer implements RpcServer
{
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private final ExecutorService threadPool;
    private final String host;
    private final int port;
    private CommonSerializer serializer;
    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    private RequestHandler requestHandler = new RequestHandler();


    //线程池初始化
    public SocketServer(String host, int port)
    {
        this.host = host;
        this.port = port;
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }

    @Override
    public <T> void publishService(T service, Class<T> serviceClass)
    {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //本地注册
        serviceProvider.addServiceProvider(service, serviceClass);
        //nacos注册
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));

        start();
    }

    //注册实现类 并立即开始监听
    //service 服务端真正的 实现类
    public void start()
    {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动....");
            Socket socket;//拿到连接的socket
            while ((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接： {} ：{} ", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler,serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer)
    {
        this.serializer = serializer;
    }




}
