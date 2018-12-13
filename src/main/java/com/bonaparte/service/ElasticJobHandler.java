package com.bonaparte.service;

import com.bonaparte.listener.ElasticJobListener;
import com.bonaparte.service.CommonSimpleElasticJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by yangmingquan on 2018/9/11.
 */
@Service
public class ElasticJobHandler {

    @Resource
    private ZookeeperRegistryCenter regCenter;

    @Resource
    private JobEventConfiguration jobEventConfiguration;

    @Autowired
    private ElasticJobListener elasticJobListener;

    private static LiteJobConfiguration.Builder simpleJobConfigBuilder(String jobName,
                                                                       Class<? extends SimpleJob> jobClass,
                                                                       int shardingTotalCount,
                                                                       String cron,
                                                                       String id) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(
                JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount).jobParameter(id).build(), jobClass.getCanonicalName()));
    }

    /**
     * 添加一个定时任务
     *
     * @param jobName            任务名
     * @param cron               表达式
     * @param shardingTotalCount 分片数
     */
    public void addJob(String jobName, String cron, Integer shardingTotalCount, String id) {
        LiteJobConfiguration jobConfig = simpleJobConfigBuilder(jobName, CommonSimpleElasticJob.class, shardingTotalCount, cron, id)
                .overwrite(true).build();

        new SpringJobScheduler(new CommonSimpleElasticJob(), regCenter, jobConfig, jobEventConfiguration, elasticJobListener).init();
    }

    public void deleteJob(String jobName){

    }
}
