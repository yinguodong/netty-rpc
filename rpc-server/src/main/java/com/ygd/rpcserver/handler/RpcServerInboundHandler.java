package com.ygd.rpcserver.handler;

import com.ygd.rpc.common.request.RpcRequest;
import com.ygd.rpc.common.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class RpcServerInboundHandler extends ChannelInboundHandlerAdapter {
    private Map<String, Object> rpcServices;

    public RpcServerInboundHandler(Map<String, Object> rpcServices){
        this.rpcServices = rpcServices;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端连接成功,{}", ctx.channel().remoteAddress());
    }

    public void channelInactive(ChannelHandlerContext ctx)   {
        log.info("客户端断开连接,{}", ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        RpcRequest rpcRequest = (RpcRequest) msg;
        log.info("接收到客户端请求, 请求接口:{}, 请求方法:{}", rpcRequest.getClassName(), rpcRequest.getMethodName());
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());
        Object result = null;
        try {
            result = this.handleRequest(rpcRequest);
            response.setResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
        }
        log.info("服务器响应:{}", response);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("连接异常");
        ctx.channel().close();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.ALL_IDLE){
                log.info("客户端已超过60秒未读写数据, 关闭连接.{}",ctx.channel().remoteAddress());
                ctx.channel().close();
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }

    private Object handleRequest(RpcRequest rpcRequest) throws Exception{
        Object bean = rpcServices.get(rpcRequest.getClassName());
        if(bean == null){
            throw new RuntimeException("未找到对应的服务: " + rpcRequest.getClassName());
        }
        Method method = bean.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        method.setAccessible(true);
        return method.invoke(bean, rpcRequest.getParameters());
    }
}
