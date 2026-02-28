package com.mipt.daniilbukreev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * The main class for the To-Do List application.
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class TodolistApplication {

    /**
     * The main method that serves as the entry point for the application.
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(TodolistApplication.class, args);
    }

}
