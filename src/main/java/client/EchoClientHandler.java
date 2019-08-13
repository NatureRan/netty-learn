package client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Logger;

/**
 * Created By nature on 2019/8/13
 */
public class EchoClientHandler extends SimpleChannelInboundHandler {

    private Logger logger = Logger.getLogger(EchoClientHandler.class.getName());

    /**
     * 服务端相应请求返回数据时会自动调用此方法，我们通过实现该方法来接受服务端返回的数据，并实现客户端调用的实现逻辑
     * @param channelHandlerContext
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        // 获取服务端返回的数据buf
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);

        // 将服务端返回的byte数组转成字符串，在控制台打印输出
        String body = new String(req, "UTF-8");
        System.out.println("receive data from server:" + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warning("发生异常" + cause.getMessage());
        ctx.close();
    }
}
