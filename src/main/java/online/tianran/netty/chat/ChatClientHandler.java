package online.tianran.netty.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author nature
 * @date 2021/7/29 7:38 下午
 */
public class ChatClientHandler extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        // 获取服务端返回的信息
        String body = (String) msg;
        System.out.println(msg);
    }
}
