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
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client channel registered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 通道建立完成发生在通道注册后面
        System.out.println("client channel connect success");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client channel unregistered");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 通道断开连接发生在通达注销之前
        System.out.println("client channel disconnect");
    }



    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        // 将byte数组转成字符串，在控制台打印输出
        String body = (String) msg;
        System.out.println("receive data from client:" + body);

        // 将接收到客户端发来的数据写回到客户端
        String response = "server receive success:" + body + "$_"; // 添加一个分隔符$_
        ByteBuf resp = Unpooled.copiedBuffer(response.getBytes());
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
