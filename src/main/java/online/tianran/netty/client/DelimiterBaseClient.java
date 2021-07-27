package online.tianran.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created By nature on 2019/8/13
 */
public class DelimiterBaseClient {

    /**
     * 字符串分隔符
     */
    private static final String DELIMITER_TAG = "@#";

    public static void main(String[] args) {
        int port = 8080;
        new DelimiterBaseClient().connect("127.0.0.1", port);
    }

    public void connect(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            // 设置DelimiterBasedFrameDecoder处理器
                            ByteBuf delimiter = Unpooled.copiedBuffer(DELIMITER_TAG.getBytes());
                            nioSocketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            // 设置StringDecoder处理器
                            nioSocketChannel.pipeline().addLast(new StringDecoder());
                            nioSocketChannel.pipeline().addLast(new DelimiterBaseClientHandler());
                        }
                    });
            // 发起异步链接操作
            ChannelFuture channelFuture = b.connect(host, port).sync();
            for (int i = 0; i < 1000; i ++) {
                // 构造客户端发送的数据ByteBuf对象
                String str = "你好，netty！" + i + DELIMITER_TAG;
                byte[] bytes = str.getBytes();
                ByteBuf buf = Unpooled.buffer(bytes.length);
                buf.writeBytes(bytes);
                // 向服务端发送数据
                ChannelFuture channelFuture1 = channelFuture.channel().writeAndFlush(buf);
                channelFuture1.syncUninterruptibly();
            }
            // 等待客户端链路关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
