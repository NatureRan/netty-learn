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
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * EchoClient启动netty客户端
 * Created By nature on 2019/8/13
 */
public class EchoClient {
    public static void main(String[] args) {
        int port = 9000;
        new EchoClient().connect("127.0.0.1", port);
    }

    public void connect(String host, int port) {
        // 创建客户端处理I/O读写的NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建客户端辅助启动类
            Bootstrap b = new Bootstrap();
            b.group(group)
                    // 设置NioSocketChannel，对应于JDK NIO类 SocketChannel 类
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            // 配置客户端处理网络I/O事件的类
//                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//                            nioSocketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            nioSocketChannel.pipeline().addLast(new FixedLengthFrameDecoder(20));
                            nioSocketChannel.pipeline().addLast(new StringDecoder());
                            nioSocketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            // 发起异步链接操作?(sync明明是同步啊)
            ChannelFuture channelFuture = b.connect(host, port).sync();
            for (int i = 0; i < 1000; i ++) {
                // 构造客户端发送的数据ByteBuf对象
                String str = "你好，netty！" + i + "$_";
                ByteBuf buf = Unpooled.copiedBuffer(str.getBytes());
                // 向服务端发送数据
                ChannelFuture channelFuture1 = channelFuture.channel().writeAndFlush(buf);
                channelFuture1.syncUninterruptibly();
            }

            // 等待客户端链路关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }
}
