package com.mm.proto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq) msg;
        if ("mm".equalsIgnoreCase(req.getUserName())) {
            System.out.println("req:" + req);
            ctx.writeAndFlush(SubscribeRespProto.SubscribeResp.newBuilder()
                    .setSubReqId(req.getSubReqId()).setCode(200).setMsg("hi").build());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
