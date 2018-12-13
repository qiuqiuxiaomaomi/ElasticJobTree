package com.bonaparte.bean;

import com.bonaparte.entity.TaskRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yangmingquan on 2018/9/11.
 */
@Repository
public class FooRepository {

    private Map<Long, TaskRepository> data = new ConcurrentHashMap<>(300, 1);

    public FooRepository() {
        init();
    }

   private void init() {
         addData(0L, 100L, "Beijing");
         addData(100L, 200L, "Shanghai");
         addData(200L, 300L, "Guangzhou");
     }

    private void addData(final long idFrom, final long idTo, final String location) {
        for (long i = idFrom; i < idTo; i++) {
            data.put(i, new TaskRepository(i, location, TaskRepository.Status.TODO));
        }
    }

    public List<TaskRepository> findTodoData(final String location, final int limit) {
        List<TaskRepository> result = new ArrayList<>(limit);
        int count = 0;
        for (Map.Entry<Long, TaskRepository> each : data.entrySet()) {
            TaskRepository foo = each.getValue();
            if (foo.getLocation().equals(location) && foo.getStatus() == TaskRepository.Status.TODO) {
                result.add(foo);
                count++;
                if (count == limit){
                    break;
                }
            }
        }
        return result;
    }

    public void setCompleted(final long id) {
        data.get(id).setStatus(TaskRepository.Status.COMPLETED);
    }
}
