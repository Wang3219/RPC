package registry;

import java.net.InetSocketAddress;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-08 22:05
 * @Description:
 */
public interface ServiceRegister {
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
