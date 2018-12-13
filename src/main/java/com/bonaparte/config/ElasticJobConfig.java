package com.bonaparte.config;

import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by yangmingquan on 2018/9/11.
 */
@Configuration
public class ElasticJobConfig {
    @Autowired
    private DataSource dataSource;

    @Bean
    public JobEventConfiguration jobEventConfiguration(){
         return new JobEventRdbConfiguration(dataSource);
    }
}
