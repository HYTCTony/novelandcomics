package com.huli.foxread.entity;

import java.util.List;

/**
 * 福利任务(阅读任务)
 */
public class WelfareReadTaskEntity extends WelfareTaskEntity{
    private String subTaskId;
    private List<ReadSubTaskBean> task;       //子任务(适配器里才拆出来重组)

    public WelfareReadTaskEntity() {
    }

    public WelfareReadTaskEntity(String id, String name, int type, String content, int status, String welfare_category_id, int reward, int is_new_man, int frequency, String number, String link, String http_image, int complete_task, String subTaskId, List<ReadSubTaskBean> task) {
        super(id, name, type, content, status, welfare_category_id, reward, is_new_man, frequency, number, link, http_image, complete_task);
        this.subTaskId = subTaskId;
        this.task = task;
    }

    public String getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(String subTaskId) {
        this.subTaskId = subTaskId;
    }

    public List<ReadSubTaskBean> getTask() {
        return task;
    }

    public void setTask(List<ReadSubTaskBean> task) {
        this.task = task;
    }
}
