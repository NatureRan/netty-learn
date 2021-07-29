package online.tianran.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @author nature
 * @date 2021/7/29 4:15 下午
 */
public class ChatClient {

    public static void main(String[] args) {
        connectChatServer("localhost", 9000);
    }

    private static void connectChatServer(String host, Integer port) {
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();
        b.group(group)
                // 设置NioSocketChannel，对应于JDK NIO类 SocketChannel 类
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast("decoder", new StringDecoder());
                        nioSocketChannel.pipeline().addLast("encoder", new StringEncoder());
                        nioSocketChannel.pipeline().addLast(new ChatClientHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = b.connect(host, port).sync();
            System.out.println("链接服务成功");
            commitBarrage(channelFuture);
            // 等待客户端链路关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    private static void commitBarrage(ChannelFuture channelFuture) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            channelFuture.channel().writeAndFlush(s).syncUninterruptibly();
        }
    }
}
