package registry.impl;

import factory.SingletonFactory;
import loalBalance.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import registry.ServiceDiscovery;
import registry.util.CuratorUtils;
import transport.dto.RpcRequest;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-08 22:25
 * @Description:
 */
@Slf4j
public class ZKServiceDiscoveryImpl implements ServiceDiscovery {
    LoadBalance loadBalance;

    public ZKServiceDiscoveryImpl() {
        this.loadBalance = SingletonFactory.getInstance(LoadBalance.class);
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest request) {
        String rpcServiceName = request.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> childrenNodes = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (childrenNodes == null || childrenNodes.size() == 0)
            throw new IllegalStateException("cannot found the address of "+ rpcServiceName);
        String serviceUrl = loadBalance.selectServiceAddress(childrenNodes, request);
        log.info("The server address {} has been found! ", serviceUrl);
        String[] socket = serviceUrl.split(":");
        String host = socket[0];
        int port = Integer.parseInt(socket[1]);
        return new InetSocketAddress(host, port);
    }
}
