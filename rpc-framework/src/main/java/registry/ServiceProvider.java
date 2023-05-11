package registry;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-11 21:10
 * @Description:
 */
public interface ServiceProvider {
    void publishService(String rpcServiceName, Object service);
    Object getService(String rpcServiceName);
}
