package github.Wang3219;

import github.Wang3219.constants.RpcConstants;
import github.Wang3219.transport.client.RpcClient;
import github.Wang3219.transport.dto.RpcMessage;
import github.Wang3219.transport.dto.RpcRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-13 19:24
 * @Description:
 */
public class ClientMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RpcClient rpcClient = new RpcClient();

        for (int i=0; i < 50; i++) {
            RpcRequest request = RpcRequest.builder()
                    .requestId("1")
                    .interfaceName("ServiceTest")
                    .methodName("add")
                    .paramTypes(new Class[]{Integer.class, Integer.class})
                    .parameters(new Object[]{i, 5})
                    .group("")
                    .version("")
                    .build();
            CompletableFuture future = (CompletableFuture) rpcClient.sendRequest(request);
            System.out.println(future.get());
        }
    }
}
