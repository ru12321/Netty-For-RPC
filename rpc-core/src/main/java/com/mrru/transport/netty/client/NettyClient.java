package com.mrru.transport.netty.client;

import com.mrru.RpcClient;
import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.enums.RpcError;
import com.mrru.exception.RpcException;
import com.mrru.factory.SingletonFactory;
import com.mrru.registry.nacos.NacosServiceDiscovery;
import com.mrru.registry.nacos.ServiceDiscovery;
import com.mrru.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

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

    private final UnprocessedRequests unprocessedRequests;

    static {
        group = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    public NettyClient(){
        this(DEFAULT_SERIALIZER);
    }

    //nacos服务对象
    public NettyClient(Integer serializerCode){
        this.serviceDiscovery = new NacosServiceDiscovery();
        this.serializer = CommonSerializer.getByCode(serializerCode);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest)
    {
        //在发送请求对象时，再判断并传入处理器（序列化器）
        if (serializer == null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();

        try {
            //从nacos发现类 获得服务地址
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            //初始化客户端，并且连接服务器，通过同步工具类countDownLatch，设置了重新连接等待机制
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);

            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener)future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    future1.channel().close();
                    //异步执行不正常结束
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }

}
