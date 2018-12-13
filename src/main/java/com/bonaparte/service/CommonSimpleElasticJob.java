package com.bonaparte.service;

import com.bonaparte.dao.mapper.TaskRepositoryMapper;
import com.bonaparte.entity.TaskRepository;
import com.bonaparte.util.CronUtils;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by yangmingquan on 2018/9/11.
 */
public class CommonSimpleElasticJob implements SimpleJob {
    @Autowired
    ElasticJobHandler elasticJobHandler;
    @Autowired
    TaskRepositoryMapper taskRepository;

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

    public void scanAddJob() {
        TaskRepository task = new TaskRepository();

        List<TaskRepository> jobTasks = taskRepository.findAll(task);
        jobTasks.forEach(jobTask -> {
            Long current = System.currentTimeMillis();
            String jobName = "job" + jobTask.getSendTime();
            String cron;
            //说明消费未发送，但是已经过了消息的发送时间，调整时间继续执行任务
            if (jobTask.getSendTime() < current) {
                //设置为一分钟之后执行，把Date转换为cron表达式
                cron = CronUtils.getCron(new Date(current + 60000));
            } else {
                cron = CronUtils.getCron(new Date(jobTask.getSendTime()));
            }
            elasticJobHandler.addJob(jobName, cron, 1, String.valueOf(jobTask.getId()));
        });
    }
}
