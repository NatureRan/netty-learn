package online.tianran.netty.demo1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import online.tianran.netty.demo1.handler.NoStickTimeClientHandler;
import online.tianran.netty.demo1.handler.StickTimeClientHandler;

/**
 * @author nature
 * @date 2021/11/28 4:47 下午
 */
public class TimeClient {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 9000;
        connect(host, port);
    }

    public static void connect(String host, int port) {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 用于处理粘包
                        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                        // 字符串解码器
                        socketChannel.pipeline().addLast(new StringDecoder());
                        socketChannel.pipeline().addLast(new NoStickTimeClientHandler());
                    }
                });
        try {
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
