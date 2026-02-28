package com.mipt.daniilbukreev.config;

import com.mipt.daniilbukreev.repository.StubTaskRepository;
import com.mipt.daniilbukreev.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration class.
 * Defines beans that are managed by the Spring container.
 */
@Configuration
public class AppConfig {

    /**
     * Creates a bean for the stub task repository.
     * @return an instance of {@link StubTaskRepository}.
     */
    @Bean
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }
}
