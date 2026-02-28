package com.mipt.daniilbukreev.beans;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * A bean with prototype scope.
 * A new instance of this bean is created every time it is requested from the container.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrototypeScopedBean {

    /**
     * Generates a unique ID for a task.
     * @return a new unique ID based on UUID.
     */
    public String generateUniqueTaskId() {
        return "TASK-" + UUID.randomUUID();
    }
}
