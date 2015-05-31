package com.test.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by apple on 15/5/17.
 */
public class GetChildrenSync implements Watcher
{

    private static final CountDownLatch latch = new CountDownLatch(1);

    private static ZooKeeper zk = null;

    @Override
    public void process(WatchedEvent watchedEvent)
    {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState())
        {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath())
            {
                latch.countDown();
            }
            else if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged)
            {
                try
                {
                    System.out.println("ReGet Child : " + zk.getChildren(watchedEvent.getPath(), true));
                }
                catch (Exception ex)
                {

                }
            }
        }
    }
}
