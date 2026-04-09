package com.mipt.daniilbukreev.repository;

import com.mipt.daniilbukreev.model.Priority;
import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.model.TaskAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        attachmentRepository.deleteAll();
    }

    @Test
    void findByCompletedAndPriority_shouldReturnCorrectTasks() {
        Task task1 = new Task("Task 1", "Desc 1", true, LocalDateTime.now(), Priority.HIGH, Set.of("tag1"));
        task1.setCreatedAt(LocalDateTime.now());
        task1.setUpdatedAt(LocalDateTime.now());
        Task task2 = new Task("Task 2", "Desc 2", false, LocalDateTime.now(), Priority.MEDIUM, Set.of("tag2"));
        task2.setCreatedAt(LocalDateTime.now());
        task2.setUpdatedAt(LocalDateTime.now());
        Task task3 = new Task("Task 3", "Desc 3", true, LocalDateTime.now(), Priority.HIGH, Set.of("tag3"));
        task3.setCreatedAt(LocalDateTime.now());
        task3.setUpdatedAt(LocalDateTime.now());

        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.persist(task3);
        entityManager.flush();

        List<Task> highPriorityCompletedTasks = taskRepository.findByCompletedAndPriority(true, Priority.HIGH);
        assertThat(highPriorityCompletedTasks).hasSize(2);
        assertThat(highPriorityCompletedTasks).containsExactlyInAnyOrder(task1, task3);
    }

    @Test
    void findTasksDueWithin7Days_shouldReturnTasksDueSoon() {
        LocalDateTime now = LocalDateTime.now().withNano(0);

        Task task1 = new Task("Due Today", "Desc", false, now.plusDays(0), Priority.HIGH, Collections.emptySet());
        task1.setCreatedAt(LocalDateTime.now());
        task1.setUpdatedAt(LocalDateTime.now());
        Task task2 = new Task("Due In 3 Days", "Desc", false, now.plusDays(3), Priority.MEDIUM, Collections.emptySet());
        task2.setCreatedAt(LocalDateTime.now());
        task2.setUpdatedAt(LocalDateTime.now());
        Task task3 = new Task("Due In 7 Days", "Desc", false, now.plusDays(7), Priority.LOW, Collections.emptySet());
        task3.setCreatedAt(LocalDateTime.now());
        task3.setUpdatedAt(LocalDateTime.now());
        Task task4 = new Task("Due In 8 Days", "Desc", false, now.plusDays(8), Priority.HIGH, Collections.emptySet());
        task4.setCreatedAt(LocalDateTime.now());
        task4.setUpdatedAt(LocalDateTime.now());
        Task task5 = new Task("Past Due", "Desc", false, now.minusDays(1), Priority.HIGH, Collections.emptySet());
        task5.setCreatedAt(LocalDateTime.now());
        task5.setUpdatedAt(LocalDateTime.now());

        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.persist(task3);
        entityManager.persist(task4);
        entityManager.persist(task5);
        entityManager.flush();

        List<Task> tasksDueSoon = taskRepository.findTasksDueWithin7Days(now, now.plusDays(7));
        assertThat(tasksDueSoon).hasSize(3);
        assertThat(tasksDueSoon).containsExactlyInAnyOrder(task1, task2, task3);
    }

    @Test
    void findAllWithAttachments_shouldReturnTasksWithEagerlyLoadedAttachments() {
        Task task = new Task("Task with Attachment", "Desc", false, LocalDateTime.now(), Priority.HIGH, Collections.emptySet());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(task);

        TaskAttachment attachment = new TaskAttachment("file.txt", "/path/to/file.txt", "text/plain", 100);
        attachment.setTask(task);
        entityManager.persist(attachment);

        entityManager.flush();
        entityManager.clear();

        List<Task> tasks = taskRepository.findAllWithAttachments();

        assertThat(tasks).hasSize(1);
        Task retrievedTask = tasks.get(0);
        assertThat(retrievedTask.getAttachments()).hasSize(1);
        assertThat(retrievedTask.getAttachments().iterator().next().getFileName()).isEqualTo("file.txt");
    }

    @Test
    void taskAndAttachmentRelationship_shouldSaveAndRetrieveCorrectly() {
        Task task = new Task("Parent Task", "Description", false, LocalDateTime.now(), Priority.HIGH, Collections.emptySet());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(task);

        TaskAttachment attachment = new TaskAttachment("rel_file.pdf", "/rel/path.pdf", "application/pdf", 200);
        attachment.setTask(task);
        entityManager.persist(attachment);
        entityManager.flush();
        entityManager.clear();

        Task retrievedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(retrievedTask.getAttachments()).hasSize(1);
        assertThat(retrievedTask.getAttachments().iterator().next().getFileName()).isEqualTo("rel_file.pdf");
    }
}
