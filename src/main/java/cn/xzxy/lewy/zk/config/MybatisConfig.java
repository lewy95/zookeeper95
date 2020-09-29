package cn.xzxy.lewy.zk.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
// 指定要扫描的Mapper类的包的路径
@MapperScan("cn.xzxy.lewy.zk.mapper")
public class MybatisConfig {

}
