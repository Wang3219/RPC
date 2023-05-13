package github.Wang3219.loalBalance;

import github.Wang3219.transport.dto.RpcRequest;

import java.util.List;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-08 21:39
 * @Description:
 */
public interface LoadBalance {
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest request);
}
