package com.mrru.transport.netty.server;

import com.mrru.RequestHandler;
import com.mrru.entity.RpcRequest;
import com.mrru.entity.RpcResponse;
import com.mrru.factory.SingletonFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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

    private final RequestHandler requestHandler;

    //获得RequestHandler单例对象
    public NettyServerHandler()
    {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception
    {
        try {
            if (msg.getHeartBeat()) {
                logger.info("接收到客户端心跳包...");
                return;
            }
            logger.info("服务器接收到请求: {}", msg);
            //拿到实现类对象
            //调用实现类对象的 封装在RpcRequest（这里是msg）中的方法
            //获得 实现类方法的返回结果
            Object result = requestHandler.handle(msg);
            //返回消息给客户端
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result, msg.getRequestId()));
            future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } finally {
            //从InBound里读取的ByteBuf要手动释放，还有自己创建的ByteBuf要自己负责释放
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

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                logger.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}