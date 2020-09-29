package cn.xzxy.lewy.zk.controller;

import cn.xzxy.lewy.zk.common.model.JsonResponseEntity;
import cn.xzxy.lewy.zk.lock.ZkDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 基于 zk Curator 实现的分布式锁>>>测试
 *
 * @author lewy95
 */
@RestController
@RequestMapping("/rest/curatorLock")
@Slf4j
public class CuratorLockController {

    @Resource
    private ZkDistributedLock zkDistributedLock;

    private final static String PATH = "test";

    @GetMapping("/lock1")
    public JsonResponseEntity getLock1() {
        zkDistributedLock.acquireDistributedLock(PATH);
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            log.error("curator操作失败，原因：{}", e.getMessage());
            // 释放锁
            zkDistributedLock.releaseDistributedLock(PATH);
            return JsonResponseEntity.buildSysTemError("执行失败");

        }
        // 释放锁
        zkDistributedLock.releaseDistributedLock(PATH);
        return JsonResponseEntity.buildOK("执行成功");
    }

    @GetMapping("/lock2")
    public JsonResponseEntity getLock2() {
        zkDistributedLock.acquireDistributedLock(PATH);
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            log.error("curator操作失败，原因：{}", e.getMessage());
            // 释放锁
            zkDistributedLock.releaseDistributedLock(PATH);
            return JsonResponseEntity.buildSysTemError("执行失败");
        }
        // 释放锁
        zkDistributedLock.releaseDistributedLock(PATH);
        return JsonResponseEntity.buildOK("执行成功");
    }
}
