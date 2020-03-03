package com.tc.curator.zkLock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author taosh
 * @create 2020-02-27 15:52
 */
public class LeaderSelectorClientB extends LeaderSelectorListenerAdapter implements Closeable {

    //当前进程
    private String name;
    //leader选举的客户端
    private LeaderSelector leaderSelector;

    CountDownLatch countDownLatch = new CountDownLatch(1);

    public void setLeaderSelector(LeaderSelector leaderSelector) {
        this.leaderSelector = leaderSelector;
    }

    public LeaderSelectorClientB(String name) {
        this.name = name;
//        leaderSelector.autoRequeue();   //自动重复参与选举
    }

    public void start(){
        leaderSelector.start();   //开始竞争leader
    }

    @Override
    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
        //如果进入当前的方法，意味着当前的进程获得锁，获得琐以后，这个方法被回调
        System.out.println(name+":现在是leader");
        countDownLatch.await();   //阻塞当前线程，防止leader丢失
    }

    @Override
    public void close() throws IOException {
        leaderSelector.close();
    }

    private static String CONNECTION_STR = "120.26.67.233:2181,120.26.67.233:2182,120.26.67.233:2183";

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(CONNECTION_STR)
                .sessionTimeoutMs(5000)                         //超时时间
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();    //重试策略

        curatorFramework.start();

        LeaderSelectorClientB leaderSelectorClient = new LeaderSelectorClientB("ClientB");
        LeaderSelector leaderSelector = new LeaderSelector(curatorFramework, "/leader", leaderSelectorClient);
        leaderSelectorClient.setLeaderSelector(leaderSelector);
        leaderSelectorClient.start();

        System.in.read();
    }
}
