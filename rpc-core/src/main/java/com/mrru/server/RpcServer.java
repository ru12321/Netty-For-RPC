package com.mrru.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @className: RpcServer
 * @author: 茹某
 * @date: 2021/8/1 10:45
 **/
public class RpcServer
{
    private final ExecutorService threadPool;

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    //线程池初始化
    public RpcServer(){
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);

    }


    //注册接口 并立即开始监听
    //service 服务端真正的 实现类
    public void register(Object service, int port){
        try(ServerSocket serverSocket = new ServerSocket(port) ){
            logger.info("服务器正在启动....");
            Socket socket;//拿到连接的socket
            while ((socket = serverSocket.accept()) != null ){
                logger.info("客户端连接！ IP为： "+ socket.getInetAddress());
                threadPool.execute(new WorkerThread(socket, service));
            }
        }catch (IOException e){
            logger.error("连接时有错误发生",e);
        }

    }



}
