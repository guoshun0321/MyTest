package com.test.netty.nio;

/**
 * Created by apple on 15/3/31.
 */
public class TimeServer
{

    public static void main(String[] args)
    {
        int port = 8080;
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-MultiplexerTimerServer-001").start();
    }

}
