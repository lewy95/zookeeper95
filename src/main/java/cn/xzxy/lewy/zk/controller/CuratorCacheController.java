package cn.xzxy.lewy.zk.controller;

import cn.xzxy.lewy.zk.common.model.JsonResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Curator Cache
 *
 * @author lewy95
 */
@RestController
@RequestMapping("/rest/curatorCache")
@Slf4j
public class CuratorCacheController {

    @Resource
    CuratorFramework curatorClient;

    /**
     * 监听指定节点本身的变化，包括节点本身的创建和节点本身数据的变化
     * 参数一：客户端
     * 参数二：监听节点
     * 参数三：是否对数据进行压缩，默认为false
     * <p>
     * 缺陷：删除节点时会报NPE，原因大概是节点已经被删除就监听不到了
     */
    @PostMapping("/nodeCacheTest")
    public JsonResponseEntity nodeCacheTest() {

        final NodeCache nodeCache = new NodeCache(curatorClient, "/klose/miro", false);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                log.info("new Node Date：" + new String(nodeCache.getCurrentData().getData()));
            }
        });

        try {
            // 创建完NodeCache的实例之后，需要调用它的start方法才能进行缓存
            // true代表缓存当前节点
            nodeCache.start(true);
            return JsonResponseEntity.buildOK("执行成功");
        } catch (Exception e) {
            log.error("curator操作失败，原因：{}", e.getMessage());
            return JsonResponseEntity.buildSysTemError("执行失败");
        }
    }

    /**
     * PathChildrenCache：用于监听子节点变化情况
     * 几种情形:
     * 1. 新增子节点
     * 2. 删除子节点
     * 3. 子节点数据变更
     * <p>
     * 参数一：客户端
     * 参数二：节点路径
     * 参数三：是否将监听变化的节点缓存在其
     * true表示客户端在接收到节点列表发生变化的同时，也能够获取到节点的数据内容
     * <p>
     * 缺陷：
     * PathChildrenCache只会监听指定节点的一级子节点，不会监听节点本身/klose，
     * 也不会监听子节点的子节点/klose/miro/opo
     */
    @PostMapping("/pathChildrenCacheTest")
    public JsonResponseEntity pathChildrenCacheTest() {

        PathChildrenCache pathChildrenCache =
                new PathChildrenCache(curatorClient, "/klose", true);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework framework, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        log.info("add childNode:" + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        log.info("childNode update:" + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        log.info("remove childNode:" + event.getData().getPath());
                        break;
                    default:
                        break;
                }
            }
        });

        try {
            //创建完pathChildrenCache的实例之后，需要调用它的start方法才能进行缓存
            pathChildrenCache.start();
            return JsonResponseEntity.buildOK("执行成功");
        } catch (Exception e) {
            log.error("curator操作失败，原因：{}", e.getMessage());
            return JsonResponseEntity.buildSysTemError("执行失败");
        }
    }

    /**
     * TreeNodeCache将NodeCache和PathChildrenCache功能结合到一起了
     * 不仅可以对子节点和父节点同时进行监听
     */
    @PostMapping("/treeCacheTest")
    public JsonResponseEntity treeCacheTest() {

        TreeCache treeNodeCache = new TreeCache(curatorClient, "/klose");
        treeNodeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case NODE_ADDED:
                        log.info("added:" + event.getData().getPath());
                        break;
                    case NODE_UPDATED:
                        log.info("updated:" + event.getData().getPath());
                        break;
                    case NODE_REMOVED:
                        log.info("removed:" + event.getData().getPath());
                        break;
                    default:
                        log.info("other:" + event.getType());
                }
            }
        });

        try {
            //创建完treeNodeCache的实例之后，需要调用它的start方法才能进行缓存
            treeNodeCache.start();
            return JsonResponseEntity.buildOK("执行成功");
        } catch (Exception e) {
            log.error("curator操作失败，原因：{}", e.getMessage());
            return JsonResponseEntity.buildSysTemError("执行失败");
        }
    }
}
