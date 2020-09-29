package cn.xzxy.lewy.zk.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * zookeeper 配置
 *
 * @author lewy95
 */
@Configuration
public class ZookeeperConfig {

    @Resource
    ZookeeperProperties zookeeperProperties;

    @Bean(initMethod = "start")
    public CuratorFramework curatorFramework() {
        return CuratorFrameworkFactory.newClient(
                zookeeperProperties.getConnectString(),
                zookeeperProperties.getSessionTimeoutMs(),
                zookeeperProperties.getConnectionTimeoutMs(),
                new RetryNTimes(zookeeperProperties.getRetryCount(), zookeeperProperties.getElapsedTimeMs()));
    }
}


