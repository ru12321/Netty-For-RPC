package com.mrru.transport.socket.server;

import com.mrru.factory.ThreadPoolFactory;
import com.mrru.registry.local.ServiceProviderImpl;
import com.mrru.registry.nacos.NacosServiceRegistry;
import com.mrru.serializer.CommonSerializer;
import com.mrru.transport.AbstractRpcServer;
import com.mrru.transport.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * Socket方式远程方法调用的提供者（服务端）
 *
 * @className: RpcServer
 * @author: 茹某
 * @date: 2021/8/1 10:45
 **/
public class SocketServer extends AbstractRpcServer
{
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private final ExecutorService threadPool;
    private final CommonSerializer serializer;
    private final RequestHandler requestHandler = new RequestHandler();

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    //线程池初始化
    public SocketServer(String host, int port, Integer serializerCode)
    {
        this.host = host;
        this.port = port;
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializerCode);
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        scanServices();
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

}
