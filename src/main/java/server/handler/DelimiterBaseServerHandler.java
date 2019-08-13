package server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created By nature on 2019/8/13
 */
public class DelimiterBaseServerHandler extends SimpleChannelInboundHandler {

    /**
     * 字符串分隔符
     */
    private static final String DELIMITER_TAG = "@#";

    /**
     * 计数器
     */
    private static final AtomicInteger counter = new AtomicInteger(0);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        // 接受客户端发送的字符串，并打印到控制台
        String content = (String) msg;
        System.out.println("received from client:" + msg + "counter:" + counter.getAndAdd(1));

        // 加入分隔符，将数据重新发送到客户端
        content += DELIMITER_TAG;
        ByteBuf buf = Unpooled.copiedBuffer(content.getBytes());
        channelHandlerContext.writeAndFlush(buf);
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
}
