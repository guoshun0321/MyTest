package com.test.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by apple on 15/5/6.
 */
public class ZKConnectionCreateWithSession implements Watcher
{

    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception
    {
        ZooKeeper zooKeeper = new ZooKeeper("localhost:4180", 5000, new ZKConnectionCreateWithSession());
        try
        {
            latch.await();
        } catch (InterruptedException ex) {
        }

        long sessionId = zooKeeper.getSessionId();
        byte[] passwd = zooKeeper.getSessionPasswd();

        zooKeeper = new ZooKeeper("localhost:4180", 5000, new ZKConnectionCreateWithSession(), 1l, "test".getBytes());

        zooKeeper = new ZooKeeper("localhost:4180", 5000, new ZKConnectionCreateWithSession(), sessionId, passwd);

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent watchedEvent)
    {
        System.out.println("Receive watched event: " + watchedEvent);
        if(Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            latch.countDown();
        }
    }
}
