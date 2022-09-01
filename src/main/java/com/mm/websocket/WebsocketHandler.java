package com.mm.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.time.LocalDateTime;


@Slf4j
public class WebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static final String url = "ws://127.0.0.1:8888/ws";

    private WebSocketServerHandshaker hs;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与客户端建立连接，通道开启！"+ ctx.channel().id());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与客户端断开连接，通道关闭！"+ ctx.channel().id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("客户端收到服务器数据：" +msg.text() + " " + ctx.channel().id());
        ctx.channel().writeAndFlush(new TextWebSocketFrame(msg.text()));
    }


}
