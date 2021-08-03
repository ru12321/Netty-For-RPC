package com.mrru.netty.server;

import com.mrru.RpcServer;
import com.mrru.codec.CommonDecoder;
import com.mrru.codec.CommonEncoder;
import com.mrru.serializer.HessianSerializer;
import com.mrru.serializer.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NIO方式服务提供侧
 * @className: NettyServer
 * @author: 茹某
 * @date: 2021/8/1 20:51
 **/
public class NettyServer implements RpcServer
{
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);


    @Override
    public void start(int port)
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO)).option(ChannelOption.SO_BACKLOG, 256).option(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true).childHandler(new ChannelInitializer<SocketChannel>()
            {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception
                {
                    ChannelPipeline pipeline = ch.pipeline();
//                                    pipeline.addLast(new CommonEncoder(new JsonSerializer())); //编码器处理器
//                                    pipeline.addLast(new CommonEncoder(new KryoSerializer()));
                    pipeline.addLast(new CommonEncoder(new HessianSerializer()));
                    pipeline.addLast(new CommonDecoder()); //解码器处理器
                    pipeline.addLast(new NettyServerHandler()); //数据处理器
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();


        } catch (Exception e) {
            logger.error("启动服务器时有错误发生： " + e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
