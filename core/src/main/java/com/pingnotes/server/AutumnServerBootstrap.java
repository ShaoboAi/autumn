package com.pingnotes.server;

import com.pingnotes.misc.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AutumnServerBootstrap implements Server {
    private static final Logger log = LoggerFactory.getLogger(AutumnServerBootstrap.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    private ChannelFuture channelFuture;

    private String ip;
    private int  port;
    private AutumnServerDef serverDef;

    @Override
    public String ip() {
        return ip;
    }

    @Override
    public int port() {
        return port;
    }

    public AutumnServerBootstrap() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerChannelInitializer())
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
    }

    public AutumnServerBootstrap(AutumnServerDef def) {
        this.serverDef = def;
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerChannelInitializer())
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
    }

    public String getIPv4() throws UnknownHostException {
        InetAddress ia = InetAddress.getLocalHost();
        return ia.getHostAddress();
    }

    public void start() throws Exception {
        int port = serverDef.getServerPort() > 0 ? serverDef.getServerPort() : Constants.serverPort;
        channelFuture = bootstrap.bind(port).syncUninterruptibly();
        this.ip = getIPv4();
        this.port = port;
    }

    public void close() {
        try {
            if (channelFuture != null){
                channelFuture.channel().close();
            }
            //todo log
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public boolean isStarted() {
        return false;
    }
}
