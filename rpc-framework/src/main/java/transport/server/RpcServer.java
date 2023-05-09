package transport.server;

import factory.SingletonFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import registry.ServiceRegister;
import registry.impl.ZKServiceRegisterImpl;
import registry.util.CuratorUtils;
import transport.codec.RpcMessageDecoder;
import transport.codec.RpcMessageEncoder;

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
    private static final int PORT = 9999;
    private final ServiceRegister serviceRegister;

    public RpcServer() {
        serviceRegister = SingletonFactory.getInstance(ZKServiceRegisterImpl.class);
    }

    public void registerService(String rpcServiceName) {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("The host is unknown!");
        }
        serviceRegister.registerService(rpcServiceName, new InetSocketAddress(host, PORT));
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
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 128)
                .handler(new LoggingHandler())
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
