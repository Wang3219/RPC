package transport.codec;

import constants.RpcConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import transport.dto.RpcMessage;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-09 11:29
 * @Description:
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        super(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
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

                    // TODO 反序列化
                }
                return rpcMessage;
            }
        }
        return decode;
    }
}
