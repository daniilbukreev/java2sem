package com.mipt.daniilbukreev.controller;

import com.mipt.daniilbukreev.dto.TaskResponseDto;
import com.mipt.daniilbukreev.mapper.TaskMapper;
import com.mipt.daniilbukreev.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
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

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long taskId, HttpSession session) {
        List<Long> favoriteIds = getFavoriteIds(session);
        if (favoriteIds.remove(taskId)) {
            session.setAttribute(FAVORITES_SESSION_KEY, favoriteIds);
        }
        return ResponseEntity.noContent().header("X-API-Version", apiVersion).build();
    }

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
