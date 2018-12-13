package com.bonaparte.config;

import com.bonaparte.constant.BonaparteProps;
import com.bonaparte.listener.ElasticJobListener;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by yangmingquan on 2018/9/11.
 */
@Configuration
public class ElasticConfig {
    @Autowired
    private BonaparteProps bonaparteProps;

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter registryCenter(){
        return new ZookeeperRegistryCenter(new ZookeeperConfiguration(bonaparteProps.getServerlists(), bonaparteProps.getNamespace()));
    }

    @Bean
    public ElasticJobListener elasticJobListener(){
        return new ElasticJobListener(100, 100);
    }
}
