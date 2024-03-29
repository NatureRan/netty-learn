package online.tianran.netty.messagePack;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Created By nature on 2019/7/10
 */
public class EchoServer {

    public static void main(String[] args) {
        int port = 9000;
        new EchoServer().bind(port);
    }

    public void bind(int port) {
        // 创建两个EventLoopGroup实例
        // EventLoopGroup是包含一组专门用于处理网络事件的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            // 创建服务端辅助启动类 ServerBootstrap 对象
            ServerBootstrap b = new ServerBootstrap();
            // 设置NIO线程组
            b.group(bossGroup, workerGroup)
                    // 设置 NioServerSocketChannel ，对应于JDK NIO类 ServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    // 设置TCP参数，连接请求的最大队列长度
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 设置io事件处理类，用来处理消息的编码以及我们的业务逻辑
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            nioSocketChannel.pipeline().addLast("msgpack decoder", new MsgPackDecoder());
                            nioSocketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
                            nioSocketChannel.pipeline().addLast("msgpack encoder", new MsgPackEncoder());
                            nioSocketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            System.out.println("echo server start...");
            // 绑定端口，等待同步成功
            ChannelFuture channelFuture = b.bind(port).sync();
            // 等待服务监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 优雅退出，释放内存资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static class EchoServerHandler extends SimpleChannelInboundHandler {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("service receive msgPack message: " + msg);
            ctx.writeAndFlush(msg);
        }
    }
}
