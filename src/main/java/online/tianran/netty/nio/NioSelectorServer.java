package online.tianran.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
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


            }
        }
    }
}
