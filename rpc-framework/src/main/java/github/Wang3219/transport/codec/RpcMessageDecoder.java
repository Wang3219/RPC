package github.Wang3219.transport.codec;

import github.Wang3219.compress.Compress;
import github.Wang3219.compress.impl.GZIPCompress;
import github.Wang3219.constants.RpcConstants;
import github.Wang3219.factory.SingletonFactory;
import github.Wang3219.transport.dto.RpcRequest;
import github.Wang3219.transport.dto.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import github.Wang3219.serialize.Serializer;
import github.Wang3219.serialize.impl.KryoSerializer;
import github.Wang3219.transport.dto.RpcMessage;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-09 11:29
 * @Description:
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    private final Serializer serializer;
    private final Compress compress;

    public RpcMessageDecoder() {
        super(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
        this.serializer = SingletonFactory.getInstance(KryoSerializer.class);
        this.compress = SingletonFactory.getInstance(GZIPCompress.class);
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
                    // 解压缩
                    body = compress.deCompress(body);
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
