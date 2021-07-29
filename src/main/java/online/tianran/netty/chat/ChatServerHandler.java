package online.tianran.netty.chat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author nature
 * @date 2021/7/29 11:36 上午
 */
public class ChatServerHandler extends SimpleChannelInboundHandler {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String msg = sdf.format(new Date()) + "【用户】" + channel.remoteAddress() + "进入直播间\n";
        channelGroup.writeAndFlush(msg);
        channelGroup.add(channel);
        System.out.println(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String msg = sdf.format(new Date()) + "【用户】" + channel.remoteAddress() + "退出直播间\n";
        channelGroup.writeAndFlush(msg);
        channelGroup.remove(channel);
        System.out.println(msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        // 读取客户端的信息
        Channel channel = ctx.channel();
        String response = sdf.format(new Date()) + "【用户】" + channel.remoteAddress() + ":" + body;
        channelGroup.writeAndFlush(response);
        System.out.println(response);
    }
}
