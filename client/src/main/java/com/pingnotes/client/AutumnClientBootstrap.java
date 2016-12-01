package com.pingnotes.client;

import com.pingnotes.proto.InternalPb;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * mail: pd-shaobo@qq.com
 */
public class AutumnClientBootstrap {
    private NioEventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private ChannelFuture channelFuture;
    private ClientHandler clientHandler;
    private AutumnClientDef clientDef;
    private int threads = 4;

    public NioEventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public String getRemoteHost() {
        return clientDef.getRemoteIp();
    }

    public int getRemotePort() {
        return clientDef.getPort();
    }

    public AutumnClientBootstrap(AutumnClientDef clientDef) {
        this.clientDef = clientDef;
        workerGroup = new NioEventLoopGroup(this.threads);
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                //server -> client
                ch.pipeline().addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
                ch.pipeline().addLast("pbRequestDecoder", new ProtobufDecoder(InternalPb.InternalResponse.getDefaultInstance()));

                //client -> server
                ch.pipeline().addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
                ch.pipeline().addLast("protobufEncoder", new ProtobufEncoder());
                clientHandler = new ClientHandler(ch.pipeline().channel());
                ch.pipeline().addLast(clientHandler);
            }
        });
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public void start() {
        channelFuture = bootstrap.connect(getRemoteHost(), getRemotePort()).syncUninterruptibly();
    }

    public void close() {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
        workerGroup.shutdownGracefully();
    }
}
