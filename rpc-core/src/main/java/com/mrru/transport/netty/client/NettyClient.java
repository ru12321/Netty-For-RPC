package com.mrru.transport.netty.client;

import com.mrru.RpcClient;
import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import com.mrru.registry.nacos.NacosServiceDiscovery;
import com.mrru.registry.nacos.ServiceDiscovery;
import com.mrru.serializer.CommonSerializer;
import com.mrru.util.RpcMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * NIO方式消费侧客户端类
 *
 * @className: NettyClient
 * @author: 茹某
 * @date: 2021/8/1 20:52
 **/
public class NettyClient implements RpcClient
{

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final Bootstrap bootstrap;
    private static final EventLoopGroup group;

    private final ServiceDiscovery serviceDiscovery;

    private final CommonSerializer serializer;

    static {
        group = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public NettyClient(){
        this(DEFAULT_SERIALIZER);
    }

    //nacos服务对象
    public NettyClient(Integer serializerCode){
        this.serviceDiscovery = new NacosServiceDiscovery();
        this.serializer = CommonSerializer.getByCode(serializerCode);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest)
    {
        //在发送请求对象时，再判断并传入处理器（序列化器）
        if (serializer == null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        /*
        而AtomicReference则对应普通的对象引用。也就是它可以保证你在修改对象引用时的线程安全性。
         */
        AtomicReference<Object> result = new AtomicReference<>(null);

        try {
            //从nacos发现类 获得服务地址
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            //初始化客户端，并且连接服务器，通过同步工具类countDownLatch，设置了重新连接等待机制
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);

            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
            channel.closeFuture().sync();

            /*
                通过 AttributeKey 的方式阻塞获得返回结果
                通过这种方式获得全局可见的返回结果，在获得返回结果 RpcResponse 后，
                将这个对象以 key 为 rpcResponse 放入 ChannelHandlerContext 中，这里就可以立刻获得结果并返回
             */
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
            RpcResponse rpcResponse = channel.attr(key).get();

            //校验请求序列号是否有变化，顺便校验响应状态码
            RpcMessageChecker.check(rpcRequest, rpcResponse);

            //set() 可以原子性的设置 AtomicReference 中的数据
            result.set(rpcResponse.getData());

        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生: ", e);
            Thread.currentThread().interrupt();
        }
        //get() 可以原子性的读取 AtomicReference 中的数据
        return result.get();
    }

}
