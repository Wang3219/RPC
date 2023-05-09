package transport.codec;

import constants.RpcConstants;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author: weiyi.wang1999@qq.com
 * @create: 2023-05-09 11:29
 * @Description:
 */
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        super(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }
}
