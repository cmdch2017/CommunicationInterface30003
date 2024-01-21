/**
 * WebClient.java
 * Created at 2022-11-07
 * Created by chenyuxiang
 * Copyright (C) 2022 WEGO Group, All rights reserved.
 */
package com.example.communicationinterface30003.client;

import com.example.communicationinterface30003.constant.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author lst
 * @date 2023/12/20 14:59
 * @return null
 */
@Slf4j
@Service
public class WebClient {

    @Value("${netty.interval:2}")
    private long interval;

    @Value("${endpoint.distribution.host}")
    private String host;
    @Value("${endpoint.distribution.port}")
    private int port;

    private Bootstrap bootstrap;

    public WebClient() {
        init();
    }

    /**
     * 初始化
     *
     * @return void
     * @author chenyuxiang
     */
    private void init() {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //加入处理器
                        ch.pipeline().addLast(new FixedLengthFrameDecoder(Constants.MEG_LENGTH));
                        ch.pipeline().addLast(new WebHandler(WebClient.this));
                    }
                });
    }

    /**
     * 连接服务端

     */
    public void connect() throws Exception {
        log.info("Web client start... ");
        // 启动客户端去连接服务端
        ChannelFuture cf = bootstrap.connect(host, port);
        cf.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    //重连交给后端线程执行
                    future.channel().eventLoop().schedule(() -> {
                        log.warn("Web client connecting...");
                        try {
                            connect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, interval, TimeUnit.SECONDS);
                } else {
                    log.info("Web client connect success");
                }
            }
        });
    }
}
