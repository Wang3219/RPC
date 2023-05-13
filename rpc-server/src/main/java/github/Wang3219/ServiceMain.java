package github.Wang3219;

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
        rpcServer.registerService("ServiceTest", serviceTest);
        rpcServer.start();
    }
}
