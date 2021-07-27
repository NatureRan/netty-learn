package online.tianran.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author nature
 * @date 2021/7/27 4:13 下午
 */
public class NioSelectorServer {

    public static void main(String[] args) throws IOException {
        // 创建NIO socketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9000));
        // 设置serverSocketChannel为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 打开selector处理channel，linux下是创建epoll
        Selector selector = Selector.open();
        // 把serverSocketChannel注册到selector上
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务启动成功");

        while (true) {
            // 阻塞等待需要处理的事件
            selector.select();
            // 获取selector中所有需要处理的事件的 SelectionKey实例
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = server.accept();
                    if (socketChannel == null) {
                        continue;
                    }
                    socketChannel.configureBlocking(false);
                    // 这里注册读时间，也可以注册写时间
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("建立连接成功");
                } else if (selectionKey.isReadable()) {
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                    int len = channel.read(byteBuffer);
                    if (len > 0) {
                        System.out.println("接收到消息:" + new String(byteBuffer.array()));
                    } else if (len == -1) {
                        // 客户端断开连接，关闭socket
                        System.out.println("客户端关闭连接");
                        channel.close();
                    }
                }

            }
        }
    }
}
