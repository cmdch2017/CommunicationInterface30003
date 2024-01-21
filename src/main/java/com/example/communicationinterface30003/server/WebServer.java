package com.example.communicationinterface30003.server;


import com.example.communicationinterface30003.constant.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.util.concurrent.Future;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class WebServer {
    @Value("${netty.thread-number:1}")
    private int threadNumber;
    @Value("${endpoint.web.port:30003}")
    private int port;
    @Autowired
    private WebServerHandler masterHandler;
    private Channel channel;
    private final EventLoopGroup GROUP = new NioEventLoopGroup(threadNumber);

    public void start() throws Exception {
        // 1、启动器，负责装配netty组件，启动服务器
        ChannelFuture channelFuture = new ServerBootstrap()
                // 2、创建 NioEventLoopGroup，可以简单理解为 线程池 + Selector
                .group(GROUP)
                // 3、选择服务器的 ServerSocketChannel 实现
                .channel(NioServerSocketChannel.class)
                // 4、child 负责处理读写，该方法决定了 child 执行哪些操作
                // ChannelInitializer 处理器（仅执行一次）
                // 它的作用是待客户端SocketChannel建立连接后，执行initChannel以便添加更多的处理器
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new FixedLengthFrameDecoder(Constants.MEG_LENGTH));
                        ch.pipeline().addLast(masterHandler);
                    }
                }).bind(port).sync();
        if (channelFuture != null && channelFuture.isSuccess()) {
            log.warn("WebServer start success, port = {}", port);
            // 获取通道
            channel = channelFuture.channel();

            channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    log.warn(future.channel().toString() + " 链路关闭");
                    // 链路关闭时，再释放线程池和连接句柄
                    GROUP.shutdownGracefully();
                }
            });
        } else {
            log.error("WebServer start failed!");
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (channel != null) {
                ChannelFuture await = channel.close().await();
                if (!await.isSuccess()) {
                    log.error("MasterServer channel close fail, {}", await.cause());
                }
            }
            Future<?> future = GROUP.shutdownGracefully().await();
            if (!future.isSuccess()) {
                log.error("MasterServer group shutdown fail, {}", future.cause());
            }
            if (log.isInfoEnabled()) {
                log.info("MasterServer shutdown success");
            }
        } catch (InterruptedException e) {
            log.error("MasterServer shutdown fail, {}", e);
        }
    }

}
