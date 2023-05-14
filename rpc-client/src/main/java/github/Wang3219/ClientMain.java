package github.Wang3219;

import github.Wang3219.config.ServiceConfig;
import github.Wang3219.proxy.RpcProxy;
import github.Wang3219.transport.client.RpcClient;
import java.util.concurrent.ExecutionException;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-13 19:24
 * @Description:
 */
public class ClientMain {
    public static void main(String[] args) {
        ServiceConfig config = ServiceConfig.builder()
                .version("version1")
                .group("group1")
                .build();

        RpcProxy rpcProxy = new RpcProxy(config);
        TestApi proxy = rpcProxy.getProxy(TestApi.class);
        for (int i=0; i < 50; i++) {
            String result = proxy.add(i, 5);
            System.out.println(result);
        }
    }
}
