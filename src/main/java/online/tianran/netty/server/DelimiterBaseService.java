package online.tianran.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import online.tianran.netty.server.handler.DelimiterBaseServerHandler;

/**
 * Created By nature on 2019/8/13
 */
public class DelimiterBaseService {

    /**
     * 字符串分隔符
     */
    private static final String DELIMITER_TAG = "@#";

    public static void main(String[] args) {
        int port = 8080;
        new DelimiterBaseService().bind(port);
    }

    public void bind(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            // 设置DelimiterBasedFrameDecoder处理器
                            ByteBuf delimiter = Unpooled.copiedBuffer(DELIMITER_TAG.getBytes());
                            nioSocketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            // 设置StringDecoder处理器
                            nioSocketChannel.pipeline().addLast(new StringDecoder());
                            nioSocketChannel.pipeline().addLast(new DelimiterBaseServerHandler());
                        }
                    });
            ChannelFuture channelFuture = b.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 优雅退出，释放内存资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
