package github.Wang3219;

import github.Wang3219.config.ServiceConfig;
import github.Wang3219.transport.server.RpcServer;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-13 19:12
 * @Description:
 */
public class ServiceMain {
    public static void main(String[] args) {
        ServiceTest serviceTest = new ServiceTest();
        RpcServer rpcServer = new RpcServer();
        ServiceConfig serviceConfig = ServiceConfig.builder()
                .group("group1")
                .version("version1")
                .service(serviceTest)
                .build();
        rpcServer.registerService(serviceConfig);
        rpcServer.start();
    }
}
