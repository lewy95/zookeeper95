package cn.xzxy.lewy.zk.controller;

import cn.xzxy.lewy.zk.common.model.JsonResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Curator CURD
 * support:
 * create / get / set / delete / exist / foreach
 * <p>
 * createAsync
 *
 * @author lewy95
 */
@RestController
@RequestMapping("/rest/curator")
@Slf4j
public class CuratorController {

    @Resource
    CuratorFramework curatorClient;

    /**
     * 获取某个节点的所有子节点名称
     */
    @PostMapping("/foreach")
    public JsonResponseEntity foreach() {

        try {
            List<String> childPaths = curatorClient.getChildren().forPath("/admin");
            //for (String childPath: childPaths) {
            //    System.out.println(childPath);
            //}
            return JsonResponseEntity.buildOK(childPaths);
        } catch (Exception e) {
            log.error("curator操作失败，原因：{}", e.getMessage());
            return JsonResponseEntity.buildOK("执行失败");
        }
    }

    /**
     * 创建节点
     * 注意点：
     * 1 除非指明创建节点的类型,默认是持久节点
     * 2 ZooKeeper规定:
     * 所有非叶子节点都是持久节点,所以递归创建出来的节点,
     * 只有最后的数据节点才是指定类型的节点,其父节点是持久节点
     */
    @PostMapping("/create")
    public JsonResponseEntity create() {

        try {
            // 创建一个初始内容为空的节点
            //curatorClient.create().forPath("/angel");
            // 创建一个带数据的节点
            // curatorClient.create().forPath("/muller", "25".getBytes());
            // 创建一个初始内容为空的临时节点
            //curatorClient.create().withMode(CreateMode.EPHEMERAL).forPath("/lahm");
            // 递归创建，/james是持久节点，/james/amber才是临时节点
            curatorClient.create().creatingParentsIfNeeded()
                    //.withMode(CreateMode.EPHEMERAL)
                    .forPath("/klose/miro","16goal".getBytes());

            return JsonResponseEntity.buildOK("执行成功");
        } catch (Exception e) {
            log.error("curator操作失败，原因：{}", e.getMessage());
            return JsonResponseEntity.buildSysTemError("执行失败");
        }
    }

    /**
     * 获取节点内容
     */
    @PostMapping("/get")
    public JsonResponseEntity get() {

        try {
            byte[] data = curatorClient.getData().forPath("/muller");

            // 提供了传入一个Stat，使用节点当前的Stat替换到传入的Stat的方法中
            // 查询方法执行完成之后，Stat引用已经执行当前最新的节点Stat
            //byte[] data = curatorClient.getData().storingStatIn(new Stat()).forPath("/muller");

            return JsonResponseEntity.buildOK(new String(data));
        } catch (Exception e) {
            log.error("curator操作失败，原因：{}", e.getMessage());
            return JsonResponseEntity.buildSysTemError("执行失败");
        }
    }

    /**
     * 更新数据
     * version版本号还是为了实现CAS并发处理，也会强制某个线程必须更新相应的版本的数据
     * <p>
     * 版本异常：
     * org.apache.zookeeper.KeeperException$BadVersionException:
     * KeeperErrorCode = BadVersion for /muller
     */
    @PostMapping("/set")
    public JsonResponseEntity set() {
        try {
            curatorClient.setData().forPath("/muller", "lisa".getBytes());
            // 指定版本更新
            //curatorClient.setData().withVersion(2).forPath("/muller", "25".getBytes());

            byte[] data = curatorClient.getData().forPath("/muller");
            return JsonResponseEntity.buildOK(new String(data));
        } catch (Exception e) {
            log.error("curator操作失败，原因：{}", e.getMessage());
            return JsonResponseEntity.buildSysTemError("执行失败");
        }
    }

    /**
     * 删除节点
     */
    @PostMapping("/delete")
    public JsonResponseEntity delete() {
        try {
            // 只能删除子节点
            curatorClient.delete().forPath("/lewy");
            // 删除一个节点，并递归删除其所有子节点
            //curatorClient.delete().deletingChildrenIfNeeded().forPath("/lewy");
            // 强制指定版本进行删除，版本号必须对应
            //curatorClient.delete().withVersion(3).forPath("/muller");
            // 由于一些网络原因，上述的删除操作有可能失败，使用guaranteed(),如果删除失败会记录下来,只要会话有效,就会不断的重试,直到删除成功为止
            //curatorClient.delete().guaranteed().forPath("/muller");

            return JsonResponseEntity.buildOK("执行成功");
        } catch (Exception e) {
            log.error("curator操作失败，原因：{}", e.getMessage());
            return JsonResponseEntity.buildSysTemError("执行失败");
        }
    }

    @PostMapping("/exist")
    public JsonResponseEntity exist() {
        try {
            // 支持目录级别
            Stat stat = curatorClient.checkExists().forPath("/muller");

            // stat 内容：
            // {
            //    "message": "OK",
            //    "code": 200,
            //    "data": {
            //        "czxid": 128849018889,
            //        "mzxid": 128849018896,
            //        "ctime": 1601385862838,
            //        "mtime": 1601387437316,
            //        "version": 1,
            //        "cversion": 0,
            //        "aversion": 0,
            //        "ephemeralOwner": 0,
            //        "dataLength": 4,
            //        "numChildren": 0,
            //        "pzxid": 128849018889
            //    },
            //    "error": null,
            //    "successful": true
            //}

            return JsonResponseEntity.buildOK(stat);

        } catch (Exception e) {
            log.error("curator操作失败，原因：{}", e.getMessage());
            return JsonResponseEntity.buildSysTemError("执行失败");
        }
    }

    @PostMapping("/createAsync")
    public JsonResponseEntity createAsync() {

        // 定义节点路径
        String path = "/create-async";

        // 定义一个线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        // 定义一个闭锁
        CountDownLatch countDownLatch = new CountDownLatch(2);

        log.info("Main thread: " + Thread.currentThread().getName());

        try {
            curatorClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
                @Override
                public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                    log.info("event[code: " + event.getResultCode() + ", type: " + event.getType() + "]");
                    log.info("Thread of processResult: " + Thread.currentThread().getName());
                    countDownLatch.countDown();
                }
            }, threadPool).forPath(path, "init".getBytes());//此处指定了线程池

            curatorClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
                @Override
                public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                    log.info("event[code: " + event.getResultCode() + ", type: " + event.getType() + "]");
                    log.info("Thread of processResult: " + Thread.currentThread().getName());
                    countDownLatch.countDown();
                }
            }).forPath(path, "init".getBytes());//此处指定了线程池

            countDownLatch.await();

            return JsonResponseEntity.buildOK("执行成功");
        } catch (Exception e) {
            log.error("curator操作失败，原因：{}", e.getMessage());
            return JsonResponseEntity.buildSysTemError("执行失败");
        } finally {
            threadPool.shutdown();
        }

        // 结果：
        // Main thread: main
        // event[code: 0, type: CREATE]
        // Thread of processResult: main-EventThread
        // event[code: -110, type: CREATE]  这里code为-110是因为/asyn-node已经存在
        // Thread of processResult: pool-3-thread-1
    }

}
