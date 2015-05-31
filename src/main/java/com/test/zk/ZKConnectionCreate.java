package com.test.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by apple on 15/5/6.
 */
public class ZKConnectionCreate implements Watcher
{

    private static CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void process(WatchedEvent watchedEvent)
    {
        System.out.println("Receive watched event: " + watchedEvent);
        if (Event.KeeperState.SyncConnected == watchedEvent.getState())
        {
            latch.countDown();
        }
    }

    public static void main(String[] args) throws Exception
    {
        ZooKeeper zooKeeper = new ZooKeeper("localhost:4180", 5000, new ZKConnectionCreate());
        System.out.println(zooKeeper.getState());
        try
        {
            latch.await();
        }
        catch (InterruptedException ex)
        {
        }
        System.out.println("Zookeeper session estableished.");
    }
}
