package com.mrru.transport.netty.client;

import com.mrru.codec.CommonDecoder;
import com.mrru.codec.CommonEncoder;
import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import com.mrru.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 用于获取 Channel 对象
 *
 * @className: ChannelProvider
 * @author: 茹某
 * @date: 2021/8/3 17:45
 **/
public class ChannelProvider
{
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    private static EventLoopGroup eventLoopGroup;

    private static Bootstrap bootstrap = initializeBootstrap();

    private static final int MAX_RETRY_COUNT = 5;
    private static Channel channel  = null;


    private static Bootstrap initializeBootstrap(){
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //是否开启 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                //TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer){
        bootstrap.handler(new ChannelInitializer<SocketChannel>()
        {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception
            {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new CommonEncoder(serializer));
                pipeline.addLast(new CommonDecoder());
                pipeline.addLast(new NettyClientHandler());
            }
        });

        /*
        countDownLatch是一个同步工具类，用来协调多个线程之间的同步，或者说起到线程之间的通信
        countDownLatch是一个计数器，线程完成一个记录一个，计数器递减，只能用一次
         */
        CountDownLatch countDownLatch = new CountDownLatch(1);

        try {
            connect(bootstrap, inetSocketAddress, countDownLatch);

            //调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行，
            //也就是 就一直等待connect连接完成
            countDownLatch.await();

        } catch (InterruptedException e) {
            logger.error("获取channel时有错误发生:", e);
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CountDownLatch countDownLatch) {
        connect(bootstrap, inetSocketAddress, MAX_RETRY_COUNT, countDownLatch);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, int retry, CountDownLatch countDownLatch) {
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("客户端连接成功!");
                channel = future.channel();

                //将count值减1
                countDownLatch.countDown();
                return;
            }

            if (retry == 0) {
                logger.error("客户端连接失败:重试次数已用完，放弃连接！");

                countDownLatch.countDown();
                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
            }
            // 第几次重连
            int order = (MAX_RETRY_COUNT - retry) + 1;
            // 本次重连的间隔
            int delay = 1 << order;
            logger.error("{}: 连接失败，第 {} 次重连……", new Date(), order);
            //每delay秒后再重新连接一次  指数退避的方式 ：delay取值 2 4 8 16
            /*
            定时任务是调用 bootstrap.config().group().schedule(),
            其中 bootstrap.config()           这个方法返回的是 BootstrapConfig，他是对 Bootstrap 配置参数的抽象，
            然后 bootstrap.config().group()   返回的就是我们在一开始的时候配置的线程模型 workerGroup，
            调   workerGroup 的 schedule      方法即可实现定时任务逻辑
             */
            bootstrap.config().group().schedule(() -> connect(bootstrap, inetSocketAddress, retry - 1, countDownLatch), delay, TimeUnit
                    .SECONDS);
        });
    }
}
