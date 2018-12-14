package com.bonaparte.entity;

import javax.persistence.*;

/**
 * Created by yangmingquan on 2018/9/11.
 */
@Entity
@Table(name = "task_repository")
public class TaskRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String content;
    /**
     * 0-未执行
     * 1-已执行
     */
    @Column
    private Integer status;
    @Column(name = "send_time")
    private Long sendTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }
}