package com.mipt.daniilbukreev.dto;

import com.mipt.daniilbukreev.model.Priority;

import java.util.Objects;

public class TaskPriorityCountDto {
    private Priority priority;
    private long count;

    public TaskPriorityCountDto(Priority priority, long count) {
        this.priority = priority;
        this.count = count;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskPriorityCountDto that = (TaskPriorityCountDto) o;
        return count == that.count && priority == that.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority, count);
    }

    @Override
    public String toString() {
        return "TaskPriorityCountDto{" +
                "priority=" + priority +
                ", count=" + count +
                '}';
    }
}
