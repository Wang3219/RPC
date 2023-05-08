package constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-08 21:15
 * @Description:
 */
@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseConstant {
    SUCCESS(200, "This remote call is successful! "),
    FAIL(500, "This remote call is fail! ");

    private int code;
    private String message;
}
