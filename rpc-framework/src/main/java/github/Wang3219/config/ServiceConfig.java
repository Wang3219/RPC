package github.Wang3219.config;

import lombok.Builder;
import lombok.Data;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-14 11:34
 * @Description:
 */
@Data
@Builder
public class ServiceConfig {
    private String version;
    private String group;
    private Object service;

    public String getServiceName() {
        Class<?> clazz = getService().getClass();
        // 没有接口
        if (clazz.getInterfaces().length == 0) {
            return clazz.getCanonicalName() + getGroup() + getVersion();
        }
        return clazz.getInterfaces()[0].getCanonicalName() + getGroup() + getVersion();
    }
}
