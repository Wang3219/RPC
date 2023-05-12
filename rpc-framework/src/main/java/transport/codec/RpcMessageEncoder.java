package transport.codec;

import constants.RpcConstants;
import factory.SingletonFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import serialize.Serializer;
import serialize.impl.KryoSerializer;
import transport.dto.RpcMessage;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-09 11:29
 * @Description:
 */
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    private final Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);

    /**
     *  4B  magic code（魔数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
     *  1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求Id）
     *  body（object类型数据）
     * @param ctx
     * @param rpcMessage
     * @param out
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        byte[] body = null;
        int fullLength = RpcConstants.HEAD_LENGTH;
        byte messageType = rpcMessage.getMessageType();
        if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            // 序列化
            body = serializer.serialize(rpcMessage.getData());
            // TODO 压缩

            fullLength += body.length;
        }
        // 编码
        out.writeBytes(RpcConstants.MAGIC_NUMBER);
        out.writeByte(RpcConstants.VERSION);
        out.writeInt(fullLength);
        out.writeByte(messageType);
        out.writeByte(rpcMessage.getCompress());
        out.writeByte(rpcMessage.getCodec());
        out.writeInt(ATOMIC_INTEGER.getAndIncrement());
        if (body != null)
            out.writeBytes(body);
    }
}
