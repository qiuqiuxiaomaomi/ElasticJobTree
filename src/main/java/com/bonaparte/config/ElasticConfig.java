package com.bonaparte.config;

import com.bonaparte.listener.ElasticJobListener;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by yangmingquan on 2018/9/11.
 */
@Configuration
public class ElasticConfig {

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter registryCenter(@Value("${elasticjob.zookeeper.server-lists}") String server,
                                                  @Value("${elasticjob.zookeeper.namespace}") String namespace){
        return new ZookeeperRegistryCenter(new ZookeeperConfiguration(server, namespace));
    }

    @Bean
    public ElasticJobListener elasticJobListener(){
        return new ElasticJobListener(100, 100);
    }
}
