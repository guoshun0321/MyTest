package com.test.zk;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by apple on 15/5/17.
 */
public class CreateNodeAsyn implements Watcher
{

    private static CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void process(WatchedEvent watchedEvent)
    {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState())
        {
            latch.countDown();
        }
    }

    private static class IStringCallback implements AsyncCallback.StringCallback
    {
        @Override
        public void processResult(int rc, String path, Object ctx, String name)
        {
            System.out.println("Create path result : " + rc + ", path" + ", " + ctx + ", real path name : " + name);
        }
    }

    public static void main(String[] args) throws Exception
    {
        ZooKeeper zk = new ZooKeeper("localhost:4180", 5000, new CreateNodeAsyn());
        latch.await();

        zk.create("/zk-test-ep-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new IStringCallback(), "I am context");
        zk.create("/zk-test-ep-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new IStringCallback(), "I am context");

        zk.create("/zk-test-ep-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, new IStringCallback(), "I am context");

        TimeUnit.SECONDS.sleep(10);
    }

}
