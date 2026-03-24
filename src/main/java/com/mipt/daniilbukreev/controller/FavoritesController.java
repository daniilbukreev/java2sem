package com.mipt.daniilbukreev.controller;

import com.mipt.daniilbukreev.dto.ErrorResponse;
import com.mipt.daniilbukreev.dto.TaskResponseDto;
import com.mipt.daniilbukreev.mapper.TaskMapper;
import com.mipt.daniilbukreev.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Favorite task management APIs using session")
public class FavoritesController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @Value("${app.version}")
    private String apiVersion;

    private static final String FAVORITES_SESSION_KEY = "favoriteTaskIds";

    public FavoritesController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @Operation(summary = "Add a task to favorites",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task added to favorites"),
                    @ApiResponse(responseCode = "404", description = "Task not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping("/{taskId}")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long taskId, HttpSession session) {
        taskService.getTaskByIdOrThrow(taskId);

        List<Long> favoriteIds = getFavoriteIds(session);
        if (!favoriteIds.contains(taskId)) {
            favoriteIds.add(taskId);
            session.setAttribute(FAVORITES_SESSION_KEY, favoriteIds);
        }
        return ResponseEntity.ok().header("X-API-Version", apiVersion).build();
    }

    @Operation(summary = "Remove a task from favorites",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task removed from favorites"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long taskId, HttpSession session) {
        List<Long> favoriteIds = getFavoriteIds(session);
        if (favoriteIds.remove(taskId)) {
            session.setAttribute(FAVORITES_SESSION_KEY, favoriteIds);
        }
        return ResponseEntity.noContent().header("X-API-Version", apiVersion).build();
    }

    @Operation(summary = "Retrieve all favorite tasks",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of favorite tasks",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponseDto.class)))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
        List<Long> favoriteIds = getFavoriteIds(session);
        List<TaskResponseDto> favoriteTasks = favoriteIds.stream()
                .map(taskService::getTaskById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok().header("X-API-Version", apiVersion).body(favoriteTasks);
    }

    @SuppressWarnings("unchecked")
    private List<Long> getFavoriteIds(HttpSession session) {
        List<Long> favoriteIds = (List<Long>) session.getAttribute(FAVORITES_SESSION_KEY);
        if (favoriteIds == null) {
            favoriteIds = new ArrayList<>();
        }
        return favoriteIds;
    }
}
