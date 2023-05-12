package transport.codec;

import constants.RpcConstants;
import factory.SingletonFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import serialize.Serializer;
import serialize.impl.KryoSerializer;
import transport.dto.RpcMessage;
import transport.dto.RpcRequest;
import transport.dto.RpcResponse;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-09 11:29
 * @Description:
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    private final Serializer serializer;
    public RpcMessageDecoder() {
        super(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
        this.serializer = SingletonFactory.getInstance(KryoSerializer.class);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) decode;
            if (buf.readableBytes() >= RpcConstants.HEAD_LENGTH) {
                // 检查魔数
                byte[] magicNumber = RpcConstants.MAGIC_NUMBER;
                byte[] mn = new byte[magicNumber.length];
                buf.readBytes(mn);
                if (!magicNumber.toString().equals(mn.toString()))
                    log.error("Decode unsuccessfully! Magic number is wrong! ");
                // 检查版本号
                byte version = buf.readByte();
                if (version != RpcConstants.VERSION)
                    log.error("Decode unsuccessfully! Version is incompatible! ");

                int fullLength = buf.readInt();
                byte messageType = buf.readByte();
                byte compressType = buf.readByte();
                byte codecType = buf.readByte();
                int requestId = buf.readInt();

                RpcMessage rpcMessage = RpcMessage.builder()
                        .compress(compressType)
                        .codec(codecType)
                        .requestId(requestId)
                        .messageType(messageType)
                        .build();
                if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                    rpcMessage.setData(RpcConstants.PING);
                    return rpcMessage;
                }
                if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                    rpcMessage.setData(RpcConstants.PONG);
                    return rpcMessage;
                }
                if (fullLength > RpcConstants.HEAD_LENGTH) {
                    byte[] body = new byte[fullLength - RpcConstants.HEAD_LENGTH];
                    buf.readBytes(body);
                    // TODO 解压缩

                    // 反序列化
                    if (messageType == RpcConstants.REQUEST_TYPE) {
                        RpcRequest request = serializer.deSerialize(body, RpcRequest.class);
                        rpcMessage.setData(request);
                    } else if (messageType == RpcConstants.RESPONSE_TYPE) {
                        RpcResponse rpcResponse = serializer.deSerialize(body, RpcResponse.class);
                        rpcMessage.setData(rpcResponse);
                    }
                }
                return rpcMessage;
            }
        }
        return decode;
    }
}
