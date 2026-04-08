package com.mipt.daniilbukreev.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Application configuration class.
 * Defines beans that are managed by the Spring container.
 */

@Configuration
@EnableJpaAuditing
public class AppConfig {

}
