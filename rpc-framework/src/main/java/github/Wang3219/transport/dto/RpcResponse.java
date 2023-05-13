package github.Wang3219.transport.dto;

import github.Wang3219.constants.RpcResponseConstant;
import lombok.*;

import java.io.Serializable;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-08 20:49
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = 469636468377226672L;
    private String requestId;
    private Integer code;
    private String message;
    private T data;

    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseConstant.SUCCESS.getCode());
        response.setMessage(RpcResponseConstant.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (data != null)
            response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail() {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseConstant.FAIL.getCode());
        response.setMessage(RpcResponseConstant.FAIL.getMessage());
        return response;
    }
}
