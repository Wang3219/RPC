package github.Wang3219.registry.impl;

import github.Wang3219.factory.SingletonFactory;
import github.Wang3219.registry.ServiceProvider;
import github.Wang3219.registry.ServiceRegister;
import lombok.extern.slf4j.Slf4j;
import github.Wang3219.transport.server.RpcServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-11 21:13
 * @Description:
 */
@Slf4j
public class ZKServiceProviderImpl implements ServiceProvider {

    private final ServiceRegister serviceRegister;
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    public ZKServiceProviderImpl() {
        serviceRegister = SingletonFactory.getInstance(ZKServiceRegisterImpl.class);
    }

    @Override
    public void publishService(String rpcServiceName, Object service) {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("The host is unknown!");
        }
        if (!serviceMap.containsKey(rpcServiceName))
            serviceMap.put(rpcServiceName, service);
        serviceRegister.registerService(rpcServiceName, new InetSocketAddress(host, RpcServer.PORT));
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (service == null)
            log.error("This service does not exist! ");
        return service;
    }
}
