package com.amaral.taskly.shared.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.amaral.taskly.shared.event.ErrorEvent;
import com.amaral.taskly.user.service.EmailService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorEventListener {

    private final EmailService emailService;

    @EventListener
    public void handleErrorEvent(ErrorEvent event) {
        try {
            log.error("Sending error email: {}", event.getSubject());
            emailService.sendHtmlEmail(
                    "amaral_adm@hotmail.com", 
                    event.getSubject(), 
                    event.getDetails()  
            );
        } catch (Exception e) {
            log.error("Failed to send error email", e);
        }
    }
}
