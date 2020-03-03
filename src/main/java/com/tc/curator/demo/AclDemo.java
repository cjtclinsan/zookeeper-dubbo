package com.tc.curator.demo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taosh
 * @create 2020-02-24 10:48
 */
public class AclDemo {
    private static String CONNECTION_STR = "120.26.67.233:2181,120.26.67.233:2182,120.26.67.233:2183";

    public static void main(String[] args) throws Exception {
        demo2();
//        demo2();
    }

    private static void demo1() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(CONNECTION_STR)
                .sessionTimeoutMs(5000)                         //超时时间
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();    //重试策略

        curatorFramework.start();

        //授权
        List<ACL> aclList = new ArrayList<>();
        ACL acl = new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.WRITE, new Id("digest", DigestAuthenticationProvider.generateDigest("admin:admin")));
        //new Id("ip", "192.168.0.1");new Id("world", "anyone");
        aclList.add(acl);

        curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath("/temp");
    }

    private static void demo2() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(CONNECTION_STR)
                .authorization("digest", "admin:admin".getBytes())
                .sessionTimeoutMs(5000)                         //超时时间
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();    //重试策略

        curatorFramework.start();

        List<ACL> aclList = new ArrayList<>();
        ACL acl = new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.WRITE, new Id("digest", DigestAuthenticationProvider.generateDigest("admin:admin")));
        aclList.add(acl);

        curatorFramework.setACL().withACL(aclList).forPath("/temp");
    }
}
