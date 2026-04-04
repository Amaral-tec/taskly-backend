package com.amaral.taskly.shared.event;

import org.springframework.context.ApplicationEvent;

public class ErrorEvent extends ApplicationEvent {

    private final String subject;
    private final String details;

    public ErrorEvent(Object source, String subject, String details) {
        super(source);
        this.subject = subject;
        this.details = details;
    }

    public String getSubject() {
        return subject;
    }

    public String getDetails() {
        return details;
    }
}
