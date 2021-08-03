package com.mrru.netty.server;

import com.mrru.RequestHandler;
import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.registry.DefaultServiceRegistry;
import com.mrru.registry.ServiceRegistry;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty中处理RpcRequest的Handler
 *
 * @className: NettyServerHandler
 * @author: 茹某
 * @date: 2021/8/2 9:34
 **/
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest>
{
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception
    {
        try {
            logger.info("服务器接收到请求：{}", msg);
            //拿到实现类对象
            String interfaceName = msg.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);

            //调用实现类对象的 封装在RpcRequest（这里是msg）中的方法
            //获得 实现类方法的返回结果
            Object result = requestHandler.handle(msg, service);

            //返回消息给客户端
            ChannelFuture channelFuture = ctx.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
            channelFuture.addListener(ChannelFutureListener.CLOSE);

        } finally {
            //从InBound里读取的ByteBuf要手动释放，还
            // 有自己创建的ByteBuf要自己负责释放。
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
