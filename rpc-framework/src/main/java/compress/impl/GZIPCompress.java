package compress.impl;

import compress.Compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-12 19:58
 * @Description:
 */
public class GZIPCompress implements Compress {
    @Override
    public byte[] compress(byte[] data) {
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(bos)) {
            gos.write(data);
            gos.flush();
            gos.finish();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip compress unsuccessfully! ", e);
        }
    }

    @Override
    public byte[] deCompress(byte[] data) {
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data))) {
            byte[] buffer = new byte[1*1024];
            int len = 0;
            while ((len=gis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("gzip deCompress unsuccessfully! ", e);
        }
    }
}
