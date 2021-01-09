package com.ygd.rpcserver.encoder;

import com.alibaba.fastjson.JSON;
import com.ygd.rpc.common.response.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 将 RpcResponse 编码成字节序列发送
 * 消息格式: Length + Content
 * Length使用int存储,标识消息体的长度
 *
 * +--------+----------------+
 * | Length |  Content       |
 * |  4字节 |   Length个字节
 * +--------+----------------+
 */
public class JsonEncoder extends MessageToByteEncoder<RpcResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse rpcResponse, ByteBuf out){
        byte[] bytes = JSON.toJSONBytes(rpcResponse);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
