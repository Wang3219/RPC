package github.Wang3219.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-08 21:35
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RpcMessage {
    private byte messageType;
    private byte codec;
    private byte compress;
    private int requestId;
    private Object data;
}
