package com.github.stepancheg.nettytd;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.function.Consumer;

/**
 * @author Stepan Koltsov
 */
public class Client {

    private final Channel channel;

    public Client(int port, final Consumer<Integer> consumer) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new IntDecoder(),
                        new IntEncoder(),
                        new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                consumer.accept((Integer) msg);
                            }
                        }
                );
            }
        });
        ChannelFuture future = bootstrap.connect("localhost", port);
        future.awaitUninterruptibly();
        if (!future.isSuccess()) {
            throw new RuntimeException(future.cause());
        }
        channel = future.channel();
    }

    public void send(int value) {
        ChannelUtil.writeAndFlush(channel, value);
    }
}
