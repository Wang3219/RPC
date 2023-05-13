package github.Wang3219.serialize;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-10 15:59
 * @Description:
 */
public interface Serializer {
    byte[] serialize(Object object);
    <T> T deSerialize(byte[] body, Class<T> clazz);
}
