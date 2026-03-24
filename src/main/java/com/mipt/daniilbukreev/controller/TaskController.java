package com.mipt.daniilbukreev.controller;

import com.mipt.daniilbukreev.dto.TaskCreateDto;
import com.mipt.daniilbukreev.dto.TaskResponseDto;
import com.mipt.daniilbukreev.dto.TaskUpdateDto;
import com.mipt.daniilbukreev.mapper.TaskMapper;
import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.service.TaskService;
import com.mipt.daniilbukreev.exception.TaskNotFoundException;
import com.mipt.daniilbukreev.validation.OnCreate;
import com.mipt.daniilbukreev.validation.OnUpdate;
import com.mipt.daniilbukreev.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing tasks.
 * Provides endpoints for CRUD operations on tasks.
 */
@RestController
@RequestMapping("/api/tasks")
@Validated
@Tag(name = "Tasks", description = "Task management APIs")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @Value("${app.version}")
    private String apiVersion;

    /**
     * Constructs a TaskController with the necessary services and scoped beans.
     * @param taskService The service for task operations.
     * @param taskMapper The mapper for converting between Task and DTOs.
     */
    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @Operation(summary = "Retrieve all tasks",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tasks",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponseDto.class)))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        List<TaskResponseDto> tasks = taskService.getAllTasks().stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .header("X-API-Version", apiVersion)
                .body(tasks);
    }

    @Operation(summary = "Retrieve a task by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task found",
                            content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Task not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(taskMapper::toResponseDto)
                .map(dto -> ResponseEntity.ok().header("X-API-Version", apiVersion).body(dto))
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Operation(summary = "Create a new task",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Task created successfully",
                            content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Validated(OnCreate.class) @RequestBody TaskCreateDto taskDto) {
        Task task = taskMapper.toEntity(taskDto);
        Task createdTask = taskService.createTask(task);
        TaskResponseDto responseDto = taskMapper.toResponseDto(createdTask);
        return ResponseEntity.created(URI.create("/api/tasks/" + responseDto.getId()))
                .header("X-API-Version", apiVersion)
                .body(responseDto);
    }

    @Operation(summary = "Update an existing task",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task updated successfully",
                            content = @Content(schema = @Schema(implementation = TaskResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Task not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id, @Validated(OnUpdate.class) @RequestBody TaskUpdateDto taskDto) {
        Task existingTask = taskService.getTaskByIdOrThrow(id);
        Task updatedTask = taskMapper.updateEntity(taskDto, existingTask);
        updatedTask.setId(id);
        taskService.updateTask(updatedTask);
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(taskMapper.toResponseDto(updatedTask));
    }

    @Operation(summary = "Delete a task by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Task not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.getTaskByIdOrThrow(id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().header("X-API-Version", apiVersion).build();
    }
}
