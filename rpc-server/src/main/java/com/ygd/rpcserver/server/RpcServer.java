package com.ygd.rpcserver.server;

import com.ygd.rpcserver.annotation.RpcService;
import com.ygd.rpcserver.decoder.JsonDecoder;
import com.ygd.rpcserver.encoder.JsonEncoder;
import com.ygd.rpcserver.handler.RpcServerInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RpcServer implements ApplicationContextAware, InitializingBean {
    // RPC服务实现容器
    private Map<String, Object> rpcServices = new HashMap<>();
    @Value("${rpc.server.port}")
    private int port;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> services = applicationContext.getBeansWithAnnotation(RpcService.class);
        for (Map.Entry<String, Object> entry : services.entrySet()) {
            Object bean = entry.getValue();
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            for (Class<?> inter : interfaces) {
                rpcServices.put(inter.getName(),  bean);
            }
        }
        log.info("加载RPC服务数量:{}", rpcServices.size());
    }

    @Override
    public void afterPropertiesSet() {
        start();
    }

    private void start(){
        new Thread(() -> {
            EventLoopGroup boss = new NioEventLoopGroup(1);
            EventLoopGroup worker = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boss, worker)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new IdleStateHandler(0, 0, 60));
                                pipeline.addLast(new JsonDecoder());
                                pipeline.addLast(new JsonEncoder());
                                pipeline.addLast(new RpcServerInboundHandler(rpcServices));
                            }
                        })
                        .channel(NioServerSocketChannel.class);
                ChannelFuture future = bootstrap.bind(port).sync();
                log.info("RPC 服务器启动, 监听端口:" + port);
                future.channel().closeFuture().sync();
            }catch (Exception e){
                e.printStackTrace();
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }
        }).start();

    }
}
