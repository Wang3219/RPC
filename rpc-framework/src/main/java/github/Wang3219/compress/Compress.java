package github.Wang3219.compress;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-12 19:57
 * @Description:
 */
public interface Compress {
    byte[] compress(byte[] data);
    byte[] deCompress(byte[] data);
}
