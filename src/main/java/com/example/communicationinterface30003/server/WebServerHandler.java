package com.example.communicationinterface30003.server;

import cn.hutool.core.util.HexUtil;
import com.example.communicationinterface30003.constant.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>ClassName: WebServerHandler</p>
 * <p>Description: 主程序入口服务器消息处理器  </p>
 *
 * @author chenyuxiang
 * @date 2022-08-30
 */
@Slf4j
@Controller
@ChannelHandler.Sharable
public class WebServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 接入的主程序入口服务
     */
    public static final Map<String, ChannelHandlerContext> MASTER_MAP = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String uuid = ctx.channel().id().asLongText();
        MASTER_MAP.put(uuid, ctx);
        log.info("连接请求进入: {}， 地址: {}", uuid, ctx.channel().remoteAddress());
        // Loop to send the three specified hexadecimal messages
        for (int i = 0; i < 10000000; i++) {
            sendHexMessage(ctx, "5c5c5c5c445566778899AABBCCDDEEFF00112233045566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABB75757575");
            Thread.sleep(100);
            sendHexMessage(ctx, "5c5c5c5c44445566778899AABBCCDDEEFF00112233045566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AACCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABB75757575");
            Thread.sleep(100);
            sendHexMessage(ctx, "5c5c5c5c445566778899AABBCCDDEEFF0011223301F400008899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF90119933445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00000000000000000001AABBCCDDEEFF00112233445566778899AABB75757575");

        }
        super.channelActive(ctx);
    }
    private void sendHexMessage(ChannelHandlerContext ctx, String hexMessage) {
        // Convert hex string to byte array
        byte[] bytes = HexUtil.decodeHex(hexMessage);

        // Create a ByteBuf and write the byte array to it
        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeBytes(bytes);

        // Send the ByteBuf
        ctx.writeAndFlush(byteBuf);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String uuid = ctx.channel().id().asLongText();
        MASTER_MAP.remove(uuid);
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            byte[] bytes = new byte[Constants.MEG_LENGTH];
            in.readBytes(bytes);

            log.info("收到消息 --> {}", HexUtil.encodeHexStr(bytes));
        } finally {
            // 释放ByteBuf
            ReferenceCountUtil.release(in);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String uuid = ctx.channel().id().asLongText();
        MASTER_MAP.remove(uuid);
        log.error(cause.getMessage());
        ctx.close();
    }

}
