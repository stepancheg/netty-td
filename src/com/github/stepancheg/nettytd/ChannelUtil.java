package com.github.stepancheg.nettytd;

import io.netty.channel.Channel;

/**
 * @author Stepan Koltsov
 */
public class ChannelUtil {

    public static void writeAndFlush(Channel channel, Object message) {
        //channel.writeAndFlush(message);
        BetterWrite.write(channel, message, null);
    }

}
