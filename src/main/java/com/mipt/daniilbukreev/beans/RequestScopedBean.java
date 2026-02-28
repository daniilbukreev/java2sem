package com.mipt.daniilbukreev.beans;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A bean with request scope.
 * A new instance of this bean is created for each HTTP request.
 */
@Component
@RequestScope
public class RequestScopedBean {

    private final String requestId = UUID.randomUUID().toString();
    private final LocalDateTime createdTime = LocalDateTime.now();

    public String getRequestId() {
        return "RequestScopedBean [ID=" + requestId + ", Created=" + createdTime + "]";
    }
}
