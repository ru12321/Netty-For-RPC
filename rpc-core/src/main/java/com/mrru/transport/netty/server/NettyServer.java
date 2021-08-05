package com.mrru.transport.netty.server;

import com.mrru.codec.CommonDecoder;
import com.mrru.codec.CommonEncoder;
import com.mrru.hook.ShutdownHook;
import com.mrru.registry.local.ServiceProviderImpl;
import com.mrru.registry.nacos.NacosServiceRegistry;
import com.mrru.serializer.CommonSerializer;
import com.mrru.transport.AbstractRpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * NIO方式服务提供侧
 *
 * @className: NettyServer
 * @author: 茹某
 * @date: 2021/8/1 20:51
 **/
public class NettyServer extends AbstractRpcServer
{
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final CommonSerializer serializer;

    public NettyServer(String host, int port){
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializerCode)
    {
        this.host = host;
        this.port = port;
        this.serializer = CommonSerializer.getByCode(serializerCode);
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        //自动扫描注册
        scanServices();
    }

    @Override
    public void start()
    {
        //启动之前，注销所有nacos实例，关闭线程池
        ShutdownHook.getShutdownHook().addClearAllHook();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO)).option(ChannelOption.SO_BACKLOG, 256)
//                    .option(ChannelOption.SO_KEEPALIVE, true) 没有意义
                    .childOption(ChannelOption.TCP_NODELAY, true).childHandler(new ChannelInitializer<SocketChannel>()
            {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception
                {
                    ChannelPipeline pipeline = ch.pipeline();
                    //Netty心跳机制 读超时时间、写超时时间、读写超时时间。
                    //当设置了readerIdleTime以后，服务端server会每隔readerIdleTime时间去检查一次channelRead方法被调用的情况，
                    // 如果在readerIdleTime时间内该channel上的channelRead()方法没有被触发，就会调用处理器的userEventTriggered方法。
                    pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                    pipeline.addLast(new CommonEncoder(serializer));    //编码器处理器--多种序列化器选择
                    pipeline.addLast(new CommonDecoder());              //解码器处理器
                    pipeline.addLast(new NettyServerHandler());         //数据处理器
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            logger.error("启动服务器时有错误发生： " + e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
