package registry.impl;

import org.apache.curator.framework.CuratorFramework;
import registry.ServiceRegister;
import registry.util.CuratorUtils;

import java.net.InetSocketAddress;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-08 22:07
 * @Description:
 */
public class ZKServiceRegisterImpl implements ServiceRegister {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
