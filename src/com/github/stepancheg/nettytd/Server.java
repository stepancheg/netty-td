package com.github.stepancheg.nettytd;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Stepan Koltsov
 */
public class Server {

    private final ServerSocketChannel channel;

    public Server() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new IntDecoder(),
                        new IntEncoder(),
                        new Handler()
                );
            }
        });
        ChannelFuture channelFuture = bootstrap.bind(0);
        channelFuture.awaitUninterruptibly();
        channel = (ServerSocketChannel) channelFuture.channel();
    }

    public int getPort() {
        return channel.localAddress().getPort();
    }

    private class Handler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ChannelUtil.writeAndFlush(ctx.channel(), msg);
        }
    }
}
