package com.mipt.daniilbukreev.controller;

import com.mipt.daniilbukreev.beans.PrototypeScopedBean;
import com.mipt.daniilbukreev.beans.RequestScopedBean;
import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.service.TaskService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing tasks.
 * Provides endpoints for CRUD operations on tasks.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final RequestScopedBean requestScopedBean;
    private final ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider;


    /**
     * Constructs a TaskController with the necessary services and scoped beans.
     * @param taskService The service for task operations.
     * @param requestScopedBean The request-scoped bean.
     * @param prototypeScopedBeanProvider The provider for prototype-scoped beans.
     */
    public TaskController(TaskService taskService, RequestScopedBean requestScopedBean, ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider) {
        this.taskService = taskService;
        this.requestScopedBean = requestScopedBean;
        this.prototypeScopedBeanProvider = prototypeScopedBeanProvider;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        return taskService.updateTask(task);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    /**
     * Endpoint to demonstrate the behavior of request and prototype scoped beans.
     * @return a string with information about the beans.
     */
    @GetMapping("/scopes")
    public String getScopeBeans() {
        String requestBeanInfo = requestScopedBean.getRequestId();
        PrototypeScopedBean prototype1 = prototypeScopedBeanProvider.getObject();
        PrototypeScopedBean prototype2 = prototypeScopedBeanProvider.getObject();
        String prototypeInfo1 = prototype1.generateUniqueTaskId();
        String prototypeInfo2 = prototype2.generateUniqueTaskId();

        return String.format(
                "Request Bean: %s <br/> Prototype Bean 1: %s <br/> Prototype Bean 2: %s",
                requestBeanInfo, prototypeInfo1, prototypeInfo2
        );
    }
}
