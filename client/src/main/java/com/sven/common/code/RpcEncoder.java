package com.sven.common.code;

import com.sven.common.util.SerializationUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by sven on 17/2/13.
 */

public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(genericClass.isInstance(o)) {
            byte[] date = SerializationUtil.serialize(o);
            byteBuf.writeInt(date.length);
            byteBuf.writeBytes(date);
        }
    }
}