package com.tc.curator.zkLock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author taosh
 * @create 2020-02-27 14:56
 */
public class LockDemo {
    private static String CONNECTION_STR = "120.26.67.233:2181,120.26.67.233:2182,120.26.67.233:2183";

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(CONNECTION_STR)
                .sessionTimeoutMs(5000)                         //超时时间
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();    //重试策略

        curatorFramework.start();

        final InterProcessMutex lock = new InterProcessMutex(curatorFramework, "/locks");

        for( int i = 0; i< 10; i ++ ){
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+"尝试竞争锁");
                try {
                    lock.acquire();   //阻塞竞争锁
                    System.out.println(Thread.currentThread().getName()+"获得了锁");

                    Thread.sleep(20000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }
}
