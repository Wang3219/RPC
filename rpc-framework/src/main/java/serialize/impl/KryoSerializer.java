package serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import serialize.Serializer;
import transport.dto.RpcRequest;
import transport.dto.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-10 16:00
 * @Description:
 */
@Slf4j
public class KryoSerializer implements Serializer {
    // kryo线程不安全，所以使用ThreadLocal
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object object) {
        try (Output output = new Output(new ByteArrayOutputStream())) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, object);
            // 删除当前线程中 ThreadLocal 对象的值，避免内存泄露
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            log.error("serialize unsuccessfully! ");
        }
        return null;
    }

    @Override
    public <T> T deSerialize(byte[] body, Class<T> clazz) {
        try (Input input = new Input(new ByteArrayInputStream(body))) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(input);
        } catch (Exception e) {
            log.error("deserialize unsuccessfully! ");
        }
        return null;
    }
}
