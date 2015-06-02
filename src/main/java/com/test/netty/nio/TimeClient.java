package com.test.netty.nio;

/**
 * Created by apple on 15/3/31.
 */
public class TimeClient
{

    public static void main(String[] args)
    {
        int port = 8080;
        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001").start();
    }

}
