package com.test.zk;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * Created by apple on 15/5/17.
 */
public class CreateNodeSync implements Watcher
{

    private static final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void process(WatchedEvent watchedEvent)
    {
        if(Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            latch.countDown();
        }
    }

    public static void main(String[] args) throws Exception
    {
        ZooKeeper zk = new ZooKeeper("localhost:4180", 5000, new CreateNodeSync());
        latch.await();

        String path1 = zk.create("/zk-test-ep-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("create path : " + path1);

        String path2 = zk.create("/zk-test-ep-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("create path : " + path2);

    }

}
