package constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-10 16:06
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum SerializerTypeConstant {

    KRYO((byte) 1, "kryo");

    private byte code;
    private String name;

    public static String getName(byte code) {
        for (SerializerTypeConstant value : SerializerTypeConstant.values()) {
            if (value.getCode() == code)
                return value.getName();
        }
        return null;
    }
}
