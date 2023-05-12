package transport.client;

import constants.CompressTypeConstant;
import constants.RpcConstants;
import constants.SerializerTypeConstant;
import factory.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import registry.ServiceDiscovery;
import registry.impl.ZKServiceDiscoveryImpl;
import transport.codec.RpcMessageDecoder;
import transport.codec.RpcMessageEncoder;
import transport.dto.RpcMessage;
import transport.dto.RpcRequest;
import transport.dto.RpcResponse;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-09 20:15
 * @Description:
 */
@Slf4j
public class RpcClient {
    private final UnprocessedRequests unprocessedRequests;
    private final ServiceDiscovery serviceDiscovery;
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public RpcClient() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        channel.pipeline().addLast(new RpcMessageEncoder());
                        channel.pipeline().addLast(new RpcMessageDecoder());
                        channel.pipeline().addLast(new RpcClientHandler());
                    }
                });
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
        serviceDiscovery = SingletonFactory.getInstance(ZKServiceDiscoveryImpl.class);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    public Object sendRequest(RpcRequest request) {
        // 返回值
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // 获取提供服务的ip:port
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(request);
        // 获取channel
        Channel channel = channelProvider.get(inetSocketAddress);
        // 若不存在则连接，我感觉这里同步异步是一样的，因为都得等着连接成功获取channel
        if (channel == null) {
            try {
                channel = bootstrap.connect(inetSocketAddress).sync().channel();
            } catch (InterruptedException e) {
                log.error("This client connect to server:{} unsuccessfully! ", inetSocketAddress);
            }
            channelProvider.set(inetSocketAddress, channel);
        }

        if (channel.isActive()) {
            unprocessedRequests.put(request.getRequestId(), resultFuture);

            RpcMessage rpcMessage = RpcMessage.builder()
                    .messageType(RpcConstants.REQUEST_TYPE)
                    .codec(SerializerTypeConstant.KRYO.getCode())
                    .compress(CompressTypeConstant.GZIP.getCode())
                    .data(request)
                    .build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
               if (future.isSuccess()) {
                   log.info("Client send message:{}", rpcMessage);
               } else {
                   resultFuture.completeExceptionally(future.cause());
                   log.error("Send message to server unsuccessfully");
               }
            });
        } else
            throw new IllegalStateException();
        return resultFuture;
    }
}
