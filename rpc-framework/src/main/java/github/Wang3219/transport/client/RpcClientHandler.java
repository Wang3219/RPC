package github.Wang3219.transport.client;

import github.Wang3219.constants.CompressTypeConstant;
import github.Wang3219.constants.RpcConstants;
import github.Wang3219.constants.SerializerTypeConstant;
import github.Wang3219.factory.SingletonFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import github.Wang3219.transport.dto.RpcMessage;
import github.Wang3219.transport.dto.RpcResponse;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-09 20:16
 * @Description:
 */
@Slf4j
public class RpcClientHandler extends ChannelInboundHandlerAdapter {
    private static final UnprocessedRequests unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcMessage) {
            byte messageType = ((RpcMessage) msg).getMessageType();
            if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                log.info("heart {}", ((RpcMessage) msg).getData());
            } else {
                RpcResponse<Object> response = (RpcResponse<Object>) ((RpcMessage) msg).getData();
                unprocessedRequests.complete(response);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {

                RpcMessage rpcMessage = RpcMessage.builder()
                        .messageType(RpcConstants.HEARTBEAT_REQUEST_TYPE)
                        .codec(SerializerTypeConstant.KRYO.getCode())
                        .compress(CompressTypeConstant.GZIP.getCode())
                        .data(RpcConstants.PING)
                        .build();
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Client encountered an exception! ");
        cause.printStackTrace();
        ctx.close();
    }
}
