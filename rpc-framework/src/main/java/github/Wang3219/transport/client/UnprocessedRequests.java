package github.Wang3219.transport.client;

import github.Wang3219.transport.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-09 20:06
 * @Description:
 */
public class UnprocessedRequests {
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_REQUESTS = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_REQUESTS.put(requestId, future);
    }

    public void complete(RpcResponse<Object> response) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_REQUESTS.remove(response.getRequestId());
        if (future != null)
            future.complete(response);
        else
            throw new IllegalStateException();
    }
}
