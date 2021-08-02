package com.mrru.netty;

import com.mrru.RpcClient;
import com.mrru.codec.CommonDecoder;
import com.mrru.codec.CommonEncoder;
import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.serializer.JsonSerializer;
import com.mrru.serializer.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @className: NettyClient
 * @author: 茹某
 * @date: 2021/8/1 20:52
 **/
public class NettyClient implements RpcClient
{

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private String host;
    private int port;
    private static final Bootstrap bootstrap;

    public NettyClient(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    static{
        EventLoopGroup group = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>()
                {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception
                    {
                        ChannelPipeline pipeline = ch.pipeline();
//                        pipeline.addLast(new CommonEncoder(new JsonSerializer())); //编码器处理器
                        pipeline.addLast(new CommonEncoder(new KryoSerializer()));
                        pipeline.addLast(new CommonDecoder()); //解码器处理器

                        ////将服务端返回的消息 放在全局的AttributeKey中
                        pipeline.addLast(new NettyClientHandler()); //数据处理器

                    }
                });
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest)
    {
        try
        {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            logger.info("客户端连接到服务器 {}：{}", host, port);

            Channel channel = channelFuture.channel();
            if (channel!=null){
                channel.writeAndFlush(rpcRequest).addListener(future -> {
                   if (future.isSuccess()){
                       logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                   }else{
                       logger.error("发送消息时有错误发生： "+future.cause());
                   }
                });
            }
            channel.closeFuture().sync();

            //通过 AttributeKey 的方式阻塞获得返回结果
            /*
            通过这种方式获得全局可见的返回结果，在获得返回结果 RpcResponse 后，
            将这个对象以 key 为 rpcResponse 放入 ChannelHandlerContext 中，这里就可以立刻获得结果并返回
             */
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            RpcResponse rpcResponse = channel.attr(key).get();

            return rpcResponse.getData();


        } catch (InterruptedException e)
        {
            logger.error("发送消息时有错误发生: ", e);
        }

        return null;
    }
}
