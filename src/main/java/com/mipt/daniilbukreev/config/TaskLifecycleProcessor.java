package com.mipt.daniilbukreev.config;

import com.mipt.daniilbukreev.repository.TaskRepository;
import com.mipt.daniilbukreev.service.TaskService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * A {@link BeanPostProcessor} that logs the lifecycle of specific beans.
 * It logs messages before and after the initialization of TaskService and TaskRepository beans.
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            System.out.println("Initializing bean: " + beanName + " of type " + bean.getClass().getSimpleName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            System.out.println("Successfully initialized bean: " + beanName);
        }
        return bean;
    }
}
