package com.github.stepancheg.nettytd;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Stepan Koltsov
 */
public class BetterWriteWithAtomic {

    private static final AttributeKey<BetterWriteWithAtomic> KEY = new AttributeKey<>(BetterWrite.class.getName());

    private final Channel channel;
    private final AtomicBoolean needFlush = new AtomicBoolean(false);

    public BetterWriteWithAtomic(final Channel channel) {
        this.channel = channel;
    }

    private void flush() {
        while (needFlush.getAndSet(false)) {
            channel.flush();
        }
    }

    public static <T> void write(Channel channel, T value, final ChannelFutureListener listener) {
        final BetterWriteWithAtomic betterWriter = getBetterWriter(channel);

        final ChannelFuture future = channel.write(value);
        if (listener != null) {
            future.addListener(listener);
        }

        if (!betterWriter.needFlush.getAndSet(true)) {
            channel.pipeline().lastContext().executor().execute(betterWriter::flush);
        }
    }

    private static BetterWriteWithAtomic getBetterWriter(final Channel channel) {
        final Attribute<BetterWriteWithAtomic> attr = channel.attr(KEY);
        BetterWriteWithAtomic betterWriter = attr.get();
        if (betterWriter != null) {
            return betterWriter;
        }
        final BetterWriteWithAtomic value = new BetterWriteWithAtomic(channel);
        final BetterWriteWithAtomic old = attr.setIfAbsent(value);
        if (old != null) {
            return old;
        }
        return value;
    }
}

