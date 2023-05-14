package github.Wang3219.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-12 20:06
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum CompressTypeConstant {

    GZIP((byte) 1, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeConstant value : CompressTypeConstant.values()) {
            if (value.getCode() == code)
                return value.getName();
        }
        return null;
    }
}
