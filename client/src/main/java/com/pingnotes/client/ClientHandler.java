package com.pingnotes.client;

import com.pingnotes.proto.InternalPb;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by shaobo on 7/6/16.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private final RequestContext requestContext;
    private final Channel channel;

    public ClientHandler(Channel channel) {
        this.channel = channel;
        this.requestContext = new RequestContext();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client handler removed");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client handler inactive");
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("client handler active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object response) throws Exception {
        if (response instanceof InternalPb.InternalResponse) {
            System.out.println("channel read " + ((InternalPb.InternalResponse) response).getResponseId());
            InternalPb.InternalResponse resp = (InternalPb.InternalResponse) response;
            onReceivedMessage(resp);
        } else {
            //todo log exception
        }
    }

    public void onReceivedMessage(InternalPb.InternalResponse response) {
        Listener listener = requestContext.remove(response.getResponseId());
        listener.onReceive(response);
    }

    public void sendMessageAsync(InternalPb.InternalRequest request, Listener listener) {
        if (channel.isWritable()) {
            requestContext.put(request.getRequestId(), listener);
            channel.writeAndFlush(request);
        } else {
            //todo log exception
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        System.out.println("damn it, some exception occur");
        cause.printStackTrace();
        ctx.close();
    }
}
