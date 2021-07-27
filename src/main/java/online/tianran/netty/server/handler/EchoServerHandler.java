package online.tianran.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * Created By nature on 2019/7/10
 */
public class EchoServerHandler extends SimpleChannelInboundHandler {

    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        // 接收客户端发来的数据，使用buf.readableBytes()获取数据大小，并转换成byte数组
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);

        // 将byte数组转成字符串，在控制台打印输出
        String body = new String(req, "UTF-8");
        System.out.println("receive data from online.tianran.netty.client:" + body);

        // 将接收到客户端发来的数据写回到客户端
        ByteBuf resp = Unpooled.copiedBuffer(body.getBytes());
        channelHandlerContext.write(resp);
    }

    /**
     * 发生异常，关闭链路
     * @param channelHandlerContext
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        channelHandlerContext.close();
    }

    /**
     * 将发送缓冲区中的消息全部写入SocketChannel中
     * @param channelHandlerContext
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
        channelHandlerContext.flush();
    }
}
