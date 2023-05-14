package github.Wang3219.proxy;

import github.Wang3219.config.ServiceConfig;
import github.Wang3219.factory.SingletonFactory;
import github.Wang3219.transport.client.RpcClient;
import github.Wang3219.transport.dto.RpcRequest;
import github.Wang3219.transport.dto.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-14 15:53
 * @Description:
 */
public class RpcProxy implements InvocationHandler {
    private final RpcClient client;
    private final ServiceConfig serviceConfig;

    public RpcProxy(ServiceConfig serviceConfig) {
        client = SingletonFactory.getInstance(RpcClient.class);
        this.serviceConfig = serviceConfig;
    }

    public <T> T getProxy(Class<T> clazz) {
        // 第二个参数为需要实现的接口，因为传进来的就是接口的class，所以直接放进数组里而不用 clazz.getInterfaces()
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .version(serviceConfig.getVersion())
                .group(serviceConfig.getGroup())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .requestId(UUID.randomUUID().toString())
                .build();
        CompletableFuture<RpcResponse<Object>> resultFuture = (CompletableFuture<RpcResponse<Object>>) client.sendRequest(request);
        return resultFuture.get().getData();
    }
}
