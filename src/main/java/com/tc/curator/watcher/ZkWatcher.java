package com.tc.curator.watcher;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * @author taosh
 * @create 2020-03-02 14:25
 */
public class ZkWatcher implements Watcher {
    static ZooKeeper zooKeeper;

    static {
        try {
            zooKeeper = new ZooKeeper("120.26.67.233:2181", 4000, new ZkWatcher());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("eventType:"+ event.getType() );
        if( event.getType() == Event.EventType.NodeDataChanged){
            try {
                zooKeeper.exists(event.getPath(), true);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
        String path = "/watcher";

        if( zooKeeper.exists(path, false) == null ){
            zooKeeper.create(path, "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        Thread.sleep(1000);
        System.out.println("--------------------");
        //true 表示使用zookeeper实例中配置的的watcher
        Stat stat = zooKeeper.exists(path, true);

        System.in.read();
    }
}
