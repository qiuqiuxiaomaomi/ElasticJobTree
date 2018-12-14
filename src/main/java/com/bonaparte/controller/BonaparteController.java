package com.bonaparte.controller;


import com.bonaparte.dao.mapper.TaskRepositoryMapper;
import com.bonaparte.entity.TaskRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@Api(value = "BonaparteController",description = "任务相关相关api")
@RestController
@RequestMapping("/task")
public class BonaparteController {

    @Autowired
    private TaskRepositoryMapper taskRepositoryMapper;

    @ApiOperation(value = "提交任务", notes = "提交任务", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")
    })
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void save() {
        TaskRepository taskRepository = new TaskRepository();
        Long unixTime = System.currentTimeMillis();
        taskRepository.setContent("test-msg-1");
        taskRepository.setStatus(0);
        taskRepository.setSendTime(unixTime);
        taskRepositoryMapper.insert(taskRepository);
    }
}
