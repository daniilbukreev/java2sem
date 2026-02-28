package com.mipt.daniilbukreev.service;

import com.mipt.daniilbukreev.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for demonstrating the use of {@link Qualifier} to inject specific beans
 * and {@link Value} to inject configuration properties.
 * It shows how to work with multiple implementations of the same interface.
 */
@Service
public class TaskStatisticsService {

    private final TaskRepository primaryRepository;
    private final TaskRepository stubRepository;

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    /**
     * Constructs a TaskStatisticsService with both primary and stub repositories.
     * @param primaryRepository The primary task repository (InMemoryTaskRepository).
     * @param stubRepository The stub task repository, explicitly injected using a qualifier.
     */
    public TaskStatisticsService(TaskRepository primaryRepository, @Qualifier("stubTaskRepository") TaskRepository stubRepository) {
        this.primaryRepository = primaryRepository;
        this.stubRepository = stubRepository;
    }

    /**
     * Prints the number of tasks in each repository to demonstrate that both are injected.
     */
    public void showRepositoryStats() {
        System.out.println("Primary repository tasks count: " + primaryRepository.findAll().size());
        System.out.println("Stub repository tasks count: " + stubRepository.findAll().size());
    }

    /**
     * Displays the application name and version injected from configuration.
     */
    public void showAppInfo() {
        System.out.printf("Application: %s, Version: %s%n", appName, appVersion);
    }
}
