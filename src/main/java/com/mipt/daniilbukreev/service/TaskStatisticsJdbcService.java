package com.mipt.daniilbukreev.service;

import com.mipt.daniilbukreev.dto.TaskPriorityCountDto;
import com.mipt.daniilbukreev.model.Priority;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class TaskStatisticsJdbcService {

    private final JdbcTemplate jdbcTemplate;

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TaskPriorityCountDto> getTasksCountByPriority() {
        String sql = "SELECT priority, COUNT(*) as task_count FROM tasks GROUP BY priority";
        return jdbcTemplate.query(sql, new TaskPriorityCountMapper());
    }

    private static class TaskPriorityCountMapper implements RowMapper<TaskPriorityCountDto> {
        @Override
        public TaskPriorityCountDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TaskPriorityCountDto(
                    Priority.valueOf(rs.getString("priority")),
                    rs.getLong("task_count")
            );
        }
    }
}
