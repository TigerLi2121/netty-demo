package com.mm.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

public class WebsocketHandler extends SimpleChannelInboundHandler<Object> {

    public static final String url = "ws://127.0.0.1:8888/ws";

    private WebSocketServerHandshaker hs;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebsocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    // ==========websocket=========

    private void handleWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否是关闭指令
        if (frame instanceof CloseWebSocketFrame) {
            hs.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
            return;
        }
        // 判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 判断是否是文本消息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported",
                    frame.getClass().getName()));
        }
        // 返回应答消息
        String req = ((TextWebSocketFrame) frame).text();
        System.out.println(ctx.channel() +" req:"+ req);
        ctx.channel().write(new TextWebSocketFrame(req + " ,Welcome use Netty Websocket, Now time is "
                + LocalDateTime.now()));
    }
    // ==========websocket=========


    // ==========http=========

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        if (!req.decoderResult().isSuccess()
                || (!HttpHeaderValues.WEBSOCKET.equals(req.headers().get("Upgrade")))) {
            res(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        WebSocketServerHandshakerFactory wsf = new WebSocketServerHandshakerFactory(url, null, false);
        hs = wsf.newHandshaker(req);
        if (hs == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            hs.handshake(ctx.channel(), req);
        }

    }

    private static void res(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        if (res.status().code() != HttpResponseStatus.OK.code()) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (res.status().code() != HttpResponseStatus.OK.code()) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
    // ==========http=========

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
