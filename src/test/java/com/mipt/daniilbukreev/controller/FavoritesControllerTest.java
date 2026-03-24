package com.mipt.daniilbukreev.controller;

import com.mipt.daniilbukreev.dto.TaskResponseDto;
import com.mipt.daniilbukreev.mapper.TaskMapper;
import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoritesController.class)
public class FavoritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    private static final String FAVORITES_SESSION_KEY = "favoriteTaskIds";

    @Test
    void addToFavorites_ShouldAddTaskToSession() throws Exception {
        given(taskService.getTaskByIdOrThrow(1L)).willReturn(new Task());
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/favorites/1").session(session))
                .andExpect(status().isOk());

        List<Long> favoriteIds = (List<Long>) session.getAttribute(FAVORITES_SESSION_KEY);
        assertTrue(favoriteIds.contains(1L));
        assertEquals(1, favoriteIds.size());
    }

    @Test
    void getFavorites_ShouldReturnTasksFromSession() throws Exception {
        MockHttpSession session = new MockHttpSession();
        List<Long> favoriteIds = new ArrayList<>();
        favoriteIds.add(1L);
        session.setAttribute(FAVORITES_SESSION_KEY, favoriteIds);

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Favorite Task");
        TaskResponseDto responseDto = new TaskResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Favorite Task");

        given(taskService.getTaskById(1L)).willReturn(Optional.of(task));
        given(taskMapper.toResponseDto(task)).willReturn(responseDto);

        mockMvc.perform(get("/api/favorites").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Favorite Task")));
    }

    @Test
    void removeFromFavorites_ShouldRemoveTaskFromSession() throws Exception {
        MockHttpSession session = new MockHttpSession();
        List<Long> favoriteIds = new ArrayList<>();
        favoriteIds.add(1L);
        favoriteIds.add(2L);
        session.setAttribute(FAVORITES_SESSION_KEY, favoriteIds);

        mockMvc.perform(delete("/api/favorites/1").session(session))
                .andExpect(status().isNoContent());

        List<Long> updatedFavoriteIds = (List<Long>) session.getAttribute(FAVORITES_SESSION_KEY);
        assertTrue(updatedFavoriteIds.contains(2L));
        assertEquals(1, updatedFavoriteIds.size());
    }
}
