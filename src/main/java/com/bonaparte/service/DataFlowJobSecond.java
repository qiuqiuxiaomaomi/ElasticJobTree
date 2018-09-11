package com.bonaparte.service;

import com.bonaparte.bean.FooRepository;
import com.bonaparte.entity.Foo;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by yangmingquan on 2018/9/11.
 */
public class DataFlowJobSecond implements DataflowJob<Foo>{

    @Resource
    private FooRepository fooRepository;

    @Override
    public List<Foo> fetchData(final ShardingContext shardingContext) {
        System.out.println(String.format("Item: %s | Time: %s | Thread: %s | %s",
                shardingContext.getShardingItem(), new SimpleDateFormat("HH:mm:ss").format(new Date()), Thread.currentThread().getId(), "DATAFLOW FETCH"));
        return fooRepository.findTodoData(shardingContext.getShardingParameter(), 10);
    }

    @Override
    public void processData(final ShardingContext shardingContext, final List<Foo> data) {
        System.out.println(String.format("Item: %s | Time: %s | Thread: %s | %s",
                shardingContext.getShardingItem(), new SimpleDateFormat("HH:mm:ss").format(new Date()), Thread.currentThread().getId(), "DATAFLOW PROCESS"));
        for (Foo each : data) {
            fooRepository.setCompleted(each.getId());
        }
    }
}
