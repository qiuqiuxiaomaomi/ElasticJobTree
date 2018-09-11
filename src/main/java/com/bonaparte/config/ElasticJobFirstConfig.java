package com.bonaparte.config;

import com.bonaparte.service.ElasticJobFirst;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
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
public class ElasticJobFirstConfig {
    @Autowired
    private ElasticConfig elasticConfig;
    @Autowired
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    public ElasticJobFirstConfig(){

    }

    @Bean
    public SimpleJob simpleJob(){
        return new ElasticJobFirst();
    }

    @Bean(initMethod = "init")
    public JobScheduler simpleJobScheduler(final SimpleJob simpleJob, @Value("${elasticJob.cron}") final String cron,
                                           @Value("${elasticJob.shardingTotalCount}") final int shardingTotalCount,
                                           @Value("${elasticJob.shardingItemParameters}") final String shardingItemParameters){
        return new SpringJobScheduler(simpleJob, zookeeperRegistryCenter, getLiteJobConfiguration(simpleJob.getClass(),
                cron, shardingTotalCount, shardingItemParameters));
    }

    public LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass,
                                                        final String cron,
                                                        final int shardingTotalCount,
                                                        final String shardingItemParameters) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(
                JobCoreConfiguration.newBuilder(jobClass.getName(), cron,
                        shardingTotalCount).shardingItemParameters(shardingItemParameters).build(), jobClass.getCanonicalName()))
                .overwrite(true)
                .build();
    }

}
