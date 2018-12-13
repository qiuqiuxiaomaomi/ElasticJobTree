package com.bonaparte;

import com.bonaparte.service.CommonSimpleElasticJob;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;

/**
 * Created by yangmingquan on 2018/9/11.
 *
 */
@SpringBootApplication
@EnableTransactionManagement
public class Application implements CommandLineRunner {
    @Resource
    private CommonSimpleElasticJob elasticJobService;

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        elasticJobService.scanAddJob();
    }
}
