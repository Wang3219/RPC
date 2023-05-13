package github.Wang3219.loalBalance;

import github.Wang3219.transport.dto.RpcRequest;

import java.util.List;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-08 21:41
 * @Description:
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceUrlList, RpcRequest request) {
        if (serviceUrlList == null || serviceUrlList.isEmpty())
            return null;
        if (serviceUrlList.size() == 1)
            return serviceUrlList.get(0);
        return doSelect(serviceUrlList, request);
    }

    protected abstract String doSelect(List<String> serviceUrlList, RpcRequest request);
}
