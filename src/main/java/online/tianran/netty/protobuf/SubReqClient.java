package online.tianran.netty.protobuf;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.Arrays;

public class SubReqClient {
    public static void main(String[] args) {
        int port = 9000;
        new SubReqClient().connect("127.0.0.1", port);
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
                            nioSocketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            nioSocketChannel.pipeline().addLast(new ProtobufDecoder(SubscribeRespProto.SubscribeResp.getDefaultInstance()));
                            nioSocketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            nioSocketChannel.pipeline().addLast(new ProtobufEncoder());
                            nioSocketChannel.pipeline().addLast(new SubReqClientHandler());
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

    public static class SubReqClientHandler extends SimpleChannelInboundHandler {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            SubscribeRespProto.SubscribeResp resp = (SubscribeRespProto.SubscribeResp) msg;
            System.out.println("receive resp : " + resp.toString());
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            for (int i = 0; i < 10; i++) {
                ctx.write(subReq(i));
            }
            ctx.flush();
        }

        private SubscribeReqProto.SubscribeReq subReq(int i) {
            SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
            builder.setSubReqID(i);
            builder.setUserName("tianran");
            builder.setProductName("netty book");
            builder.addAllAddress(Arrays.asList("Nanjing", "Yixing", "Hangzhou"));
            return builder.build();
        }
    }
}
