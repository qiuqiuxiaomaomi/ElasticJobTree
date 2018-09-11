package com.bonaparte.config;

import com.bonaparte.service.SpringDataflowJob;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Created by yangmingquan on 2018/9/11.
 */
@Configuration
public class DataflowJobConfig {

    @Resource
    private ZookeeperRegistryCenter regCenter;

    @Resource
    private JobEventConfiguration jobEventConfiguration;

    @Bean
    public DataflowJob dataflowJob() {
        return new SpringDataflowJob();
    }

    @Bean(initMethod = "init")
    public JobScheduler dataflowJobScheduler(final DataflowJob dataflowJob, @Value("${elasticJob.cron}") final String cron, @Value("${elasticJob.shardingTotalCount}") final int shardingTotalCount,
                                             @Value("${elasticJob.shardingItemParameters}") final String shardingItemParameters) {
        return new SpringJobScheduler(dataflowJob, regCenter, getLiteJobConfiguration(dataflowJob.getClass(), cron, shardingTotalCount, shardingItemParameters), jobEventConfiguration);
    }

    /**
     *  作业配置
     *  作业配置分为3级，分别是JobCoreConfiguration，JobTypeConfiguration和LiteJobConfiguration。
     *  LiteJobConfiguration使用JobTypeConfiguration，JobTypeConfiguration使用JobCoreConfiguration，层层嵌套。
     *  JobTypeConfiguration根据不同实现类型分为SimpleJobConfiguration，DataflowJobConfiguration和ScriptJobConfiguration。
     */
    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends DataflowJob> jobClass, final String cron, final int shardingTotalCount, final String shardingItemParameters) {
        return LiteJobConfiguration.newBuilder(new DataflowJobConfiguration(JobCoreConfiguration.newBuilder(
                jobClass.getName(), cron, shardingTotalCount).shardingItemParameters(shardingItemParameters).build(), jobClass.getCanonicalName(), true)).overwrite(true).build();
    }
}
