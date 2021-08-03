package com.mrru.socket.server;

import com.mrru.RpcServer;
import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import com.mrru.registry.ServiceRegistry;
import com.mrru.RequestHandler;
import com.mrru.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private final ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    private RequestHandler requestHandler = new RequestHandler();


    //线程池初始化
    public SocketServer(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }


    //注册实现类 并立即开始监听
    //service 服务端真正的 实现类
    public void start(int port)
    {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动....");
            Socket socket;//拿到连接的socket
            while ((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接： {} ：{} ", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry, serializer));
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
