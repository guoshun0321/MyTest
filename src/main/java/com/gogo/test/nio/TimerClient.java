package com.gogo.test.nio;

/**
 * Created by 80107436 on 2015-06-26.
 */
public class TimerClient {

    public static void main(String[] args) {
        int port = 8080;

        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001").start();
    }

}
