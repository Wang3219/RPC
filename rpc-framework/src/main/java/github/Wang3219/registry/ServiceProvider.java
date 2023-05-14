package github.Wang3219.registry;

import github.Wang3219.config.ServiceConfig;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-11 21:10
 * @Description:
 */
public interface ServiceProvider {
    void publishService(ServiceConfig serviceConfig);
    Object getService(String rpcServiceName);
}
