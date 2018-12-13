package com.bonaparte.dao.mapper;

import com.bonaparte.entity.TaskRepository;
import com.bonaparte.util.MyMapper;

import java.util.List;

public interface TaskRepositoryMapper extends MyMapper<TaskRepository> {

    public List<TaskRepository> findAll(TaskRepository taskRepository);
}
