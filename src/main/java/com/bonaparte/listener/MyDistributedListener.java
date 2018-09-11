package com.bonaparte.listener;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by yangmingquan on 2018/9/11.
 */
public class MyDistributedListener extends AbstractDistributeOnceElasticJobListener {
    private final Logger logger = LoggerFactory.getLogger(MyDistributedListener.class);



    public MyDistributedListener(long startedTimeoutMilliseconds, long completedTimeoutMilliseconds) {
        super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
    }

    @Override
    public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
        logger.info("=====================分布式监听器任务开始");
    }

    @Override
    public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
        logger.info("=====================分布式监听器任务结束");
    }
}
