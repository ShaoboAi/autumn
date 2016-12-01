package com.pingnotes.server;

import com.pingnotes.proto.InternalPb;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * mail: pd-shaobo@qq.com
 */
public class ServerChannelInitializer extends ChannelInitializer {
    private final EventExecutorGroup bizLogicExecutor;
    private int nThreads;
    private int idleTime;

    public ServerChannelInitializer() {
        nThreads = 100;
        idleTime = 60 * 10;
        bizLogicExecutor = new DefaultEventExecutorGroup(nThreads);
    }

    public ServerChannelInitializer(AutumnServerDef config) {
        this.nThreads =  config.getThreadNum();
        this.idleTime = config.getIdleTime();
        bizLogicExecutor = new DefaultEventExecutorGroup(this.nThreads);
    }


    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(0, 0, idleTime));
        //client -> server
        ch.pipeline().addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
        ch.pipeline().addLast("pbRequestDecoder", new ProtobufDecoder(InternalPb.InternalRequest.getDefaultInstance()));
        //server -> client
        ch.pipeline().addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
        ch.pipeline().addLast("protobufEncoder", new ProtobufEncoder());
        ch.pipeline().addLast(bizLogicExecutor, new DispatchingHandler());
    }
}
