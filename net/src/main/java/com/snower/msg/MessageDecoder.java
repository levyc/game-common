package com.snower.msg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
      List<Object> list) throws Exception {

  }


}
