package transport.client;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-09 19:54
 * @Description:
 */
public class ChannelProvider {
    private static final Map<String, Channel> CHANNEL_PROVIDER = new ConcurrentHashMap<>();

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        CHANNEL_PROVIDER.put(inetSocketAddress.toString(), channel);
    }

    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if (CHANNEL_PROVIDER.containsKey(key)) {
            Channel channel = CHANNEL_PROVIDER.get(key);
            if (channel != null && channel.isActive())
                return channel;
            // 当前key对应的channel有问题就删掉
            CHANNEL_PROVIDER.remove(key);
        }
        return null;
    }
}
