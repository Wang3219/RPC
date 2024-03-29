package github.Wang3219.transport.server;

import github.Wang3219.config.ServiceConfig;
import github.Wang3219.factory.SingletonFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import github.Wang3219.registry.ServiceProvider;
import github.Wang3219.registry.impl.ZKServiceProviderImpl;
import github.Wang3219.registry.util.CuratorUtils;
import github.Wang3219.transport.codec.RpcMessageDecoder;
import github.Wang3219.transport.codec.RpcMessageEncoder;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-09 10:59
 * @Description:
 */
@Slf4j
public class RpcServer {
    public static final int PORT = 9999;
    private final ServiceProvider serviceProvider;

    public RpcServer() {
        this.serviceProvider = SingletonFactory.getInstance(ZKServiceProviderImpl.class);
    }

    public void registerService(ServiceConfig serviceConfig) {
        serviceProvider.publishService(serviceConfig);
    }

    public void start() {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("The host is unknown!");
        }
        // 程序退出时清空当前ip:port注册的所有服务
        log.info("clear all the service of {}:{}", host, PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(host, PORT);
            CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
        }));

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandleGroup = new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors() * 2);

        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        channel.pipeline().addLast(new RpcMessageEncoder());
                        channel.pipeline().addLast(new RpcMessageDecoder());
                        channel.pipeline().addLast(serviceHandleGroup, new RpcServerHandler());
                    }
                });
        try {
            //等待绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(host, PORT).sync();
            // 等待端口关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Some mistakes have been happened when start the server:{}", host);
        } finally {
            log.info("Shutdown all the groups! ");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandleGroup.shutdownGracefully();
        }
    }
}
