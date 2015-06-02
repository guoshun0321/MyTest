package com.test.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by apple on 15/3/31.
 */
public class MultiplexerTimeServer implements Runnable
{

    private Selector selector;

    private ServerSocketChannel servChannel;

    private volatile boolean stop;

    public MultiplexerTimeServer(int port)
    {
        try
        {
            selector = Selector.open();

            servChannel = ServerSocketChannel.open();
            servChannel.configureBlocking(false);
            servChannel.socket().bind(new InetSocketAddress(port), 1024);
            servChannel.register(selector, SelectionKey.OP_ACCEPT);

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void stop()
    {
        this.stop = true;
    }

    public void run()
    {
        while (!stop)
        {
            try
            {
                selector.select(1000);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext())
                {
                    key = it.next();
                    it.remove();
                    try
                    {
                        handleInput(key);
                    }
                    catch (Exception ex)
                    {
                        if (key != null)
                        {
                            key.cancel();
                            if (key.channel() != null)
                            {
                                key.channel().close();
                            }
                        }
                    }
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
        }
        if (selector != null)
        {
            try
            {
                selector.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void handleInput(SelectionKey key) throws IOException
    {

        if (key.isValid())
        {
            if (key.isAcceptable())
            {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable())
            {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);

                if (readBytes > 0)
                {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("The time server receive order : " + body);

                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
                    doWrite(sc, currentTime);
                }
                else if (readBytes < 0)
                {
                    // 对端链路关闭
                    key.cancel();
                    sc.close();
                }
                else
                {
                    ;
                }
            }
        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException
    {
        if (response != null && response.trim().length() > 0)
        {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }

}
