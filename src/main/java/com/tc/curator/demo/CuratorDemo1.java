package com.tc.curator.demo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @author taosh
 * @create 2020-02-24 10:02
 */
public class CuratorDemo1 {

    private static String CONNECTION_STR = "120.26.67.233:2181,120.26.67.233:2182,120.26.67.233:2183";

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory
                                            .builder()
                                            .connectString(CONNECTION_STR)
                                            .sessionTimeoutMs(5000)                         //超时时间
                                            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();    //重试策略
        //ExponentialBackoffRetry   衰减重试
        //RetryOneTime  重试一次
        //RetryNtimes   最大重试N次
        //...
        curatorFramework.start();      //启动

        //curd操作   增删改查
//        curatorFramework.create();
//        curatorFramework.setACL();
//        curatorFramework.delete();
//        curatorFramework.getData();
        createData(curatorFramework);

        updateData(curatorFramework);

        deleteData(curatorFramework);
    }

    private static void createData(CuratorFramework curator) throws Exception {
        curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/data/program", "test".getBytes());
    }

    private static void updateData(CuratorFramework curator) throws Exception {
        curator.setData().forPath("/data/program", "up".getBytes());
    }

    private static void deleteData(CuratorFramework curator) throws Exception {
        Stat stat = new Stat();
        String value = new String(curator.getData().storingStatIn(stat).forPath("/data/program"));
        curator.delete().withVersion(stat.getVersion()).forPath("/data/program");
    }
}
