package com.gogo.test.nio;

/**
 * Created by 80107436 on 2015-06-26.
 */
public class TimerServer {

    public static void main(String[] args) {
        int port = 8080;
        MultiplexerTimerServer server = new MultiplexerTimerServer(port);
        new Thread(server, "NIO-TimerServer-001").start();
    }

}
