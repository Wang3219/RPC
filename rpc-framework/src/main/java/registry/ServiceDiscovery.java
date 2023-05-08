package registry;

import transport.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-08 22:22
 * @Description:
 */
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcRequest request);
}
