package transport.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-08 20:42
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -8877484331183170683L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private String version;
    private String group;

    public String getRpcServiceName() {
        return getInterfaceName() + getGroup() + getVersion();
    }
}
