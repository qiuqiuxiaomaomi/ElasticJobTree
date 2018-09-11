package com.bonaparte.service;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * Created by yangmingquan on 2018/9/11.
 */
public class ElasticJobFirst implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        //执行任务
        System.out.println(String.format("Thread ID: %s, 任务总数 %s, " +
                "当前分片项 %s,  当前参数 %s, 当前任务名称 %s, 当前任务参数 %s",
                Thread.currentThread().getId(),
                shardingContext.getShardingTotalCount(),
                shardingContext.getShardingItem(),
                shardingContext.getShardingParameter(),
                shardingContext.getJobName(),
                shardingContext.getJobParameter()));
    }
}
