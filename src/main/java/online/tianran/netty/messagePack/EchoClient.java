package online.tianran.netty.messagePack;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

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
                            nioSocketChannel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            nioSocketChannel.pipeline().addLast("msgpack decoder", new MsgPackDecoder());
                            nioSocketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
                            nioSocketChannel.pipeline().addLast("msgpack encoder", new MsgPackEncoder());
                            nioSocketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            // 发起异步链接操作?(sync明明是同步啊)
            ChannelFuture channelFuture = b.connect(host, port).sync();
            // 等待客户端链路关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    public static class EchoClientHandler extends SimpleChannelInboundHandler {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("Client receive msgpack message: " + msg);
//            ctx.writeAndFlush(msg);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            UserInfo[] userInfos = initUserInfos();
            for (UserInfo userInfo : userInfos) {
                ctx.writeAndFlush(userInfo);
            }
//            ctx.flush();
        }

        private UserInfo[] initUserInfos() {
            UserInfo[] userInfos = new UserInfo[10];
            for (int i = 0; i < 10; i++) {
                UserInfo userInfo = new UserInfo((long) i, "name" + i);
                userInfos[i] = userInfo;
            }
            return userInfos;
        }
    }
}
