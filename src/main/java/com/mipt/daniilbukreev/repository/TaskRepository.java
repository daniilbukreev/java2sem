package com.mipt.daniilbukreev.repository;

import com.mipt.daniilbukreev.model.Priority;
import com.mipt.daniilbukreev.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    @Query("SELECT t FROM Task t WHERE t.dueDate >= :now AND t.dueDate <= :sevenDaysFromNow")
    List<Task> findTasksDueWithin7Days(@Param("now") Instant now, @Param("sevenDaysFromNow") Instant sevenDaysFromNow);

    @EntityGraph(attributePaths = "attachments")
    List<Task> findAllWithAttachments();
}
