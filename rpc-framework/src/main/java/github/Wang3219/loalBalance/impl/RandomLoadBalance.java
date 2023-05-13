package github.Wang3219.loalBalance.impl;

import github.Wang3219.loalBalance.AbstractLoadBalance;
import github.Wang3219.transport.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-08 21:45
 * @Description:
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceUrlList, RpcRequest request) {
        Random random = new Random();
        return serviceUrlList.get(random.nextInt(serviceUrlList.size()));
    }
}
