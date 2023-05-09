package transport.server;

import constants.RpcConstants;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import transport.dto.RpcMessage;
import transport.dto.RpcRequest;
import transport.dto.RpcResponse;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-09 11:32
 * @Description:
 */
@Slf4j
public class RpcServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcMessage) {
            log.info("server received a message:{}", msg);
            byte messageType = ((RpcMessage) msg).getMessageType();
            RpcMessage rpcMessage = new RpcMessage();
            // TODO 编码、压缩方式
            rpcMessage.setCodec((byte) -1);
            rpcMessage.setCompress((byte) -1);

            if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                rpcMessage.setData(RpcConstants.PONG);
            } else {
                RpcRequest request = (RpcRequest) ((RpcMessage) msg).getData();
                // TODO 反射执行目标方法
                Object result = null;
                log.info("server get a result:{}", result.toString());

                rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    RpcResponse<Object> success = RpcResponse.success(result, request.getRequestId());
                    rpcMessage.setData(success);
                } else
                    log.error("The channel is unWritable");
            }
            ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("30 seconds without receiving any message, closing the connection! ");
                ctx.close();
            }
        } else
            super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Service encountered an exception! ");
        cause.printStackTrace();
        ctx.close();
    }
}
