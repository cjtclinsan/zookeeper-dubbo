package com.tc.curator.demo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author taosh
 * @create 2020-02-24 16:04
 */
public class WatcherDemo {
    private static String CONNECTION_STR = "120.26.67.233:2181,120.26.67.233:2182,120.26.67.233:2183";

    public static void main(String[] args) throws Exception {
        //PathChildCache   针对子节点得创建，删除和更新触发时间
        //NodeCache    针对于当前节点得触发时间
        //TreeCache

        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(CONNECTION_STR)
                .sessionTimeoutMs(5000)                         //超时时间
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();    //重试策略

        curatorFramework.start();

        addListenerWithChild(curatorFramework);

        System.in.read();
    }

    //配置中心
    //对创建，修改，删除监听
    private static void addListenerWithNode(CuratorFramework curator) throws Exception {
//        curator.create().withMode(CreateMode.PERSISTENT).forPath("/watch", "watch".getBytes());

        NodeCache nodeCache = new NodeCache(curator, "/watch", false);

        NodeCacheListener nodeCacheListener = () -> {
            System.out.println("receive Node Changed");
            System.out.println(nodeCache.getCurrentData().getPath()+"---"+new String(nodeCache.getCurrentData().getData()));
        };

        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start();
    }

    //针对子节点  增，删，改的监听，实现服务注册中心的时候，针对服务做动态感知
    private static void addListenerWithChild(CuratorFramework curator) throws Exception {
        PathChildrenCache childCache = new PathChildrenCache(curator, "/watch", false);

        PathChildrenCacheListener childCacheListener = (curatorFramework, pathChildernCacheEvent) -> {
            System.out.println(pathChildernCacheEvent.getType()+"---"+new String(pathChildernCacheEvent.getData().getData()));
        };

        childCache.getListenable().addListener(childCacheListener);
        childCache.start();
    }
}
