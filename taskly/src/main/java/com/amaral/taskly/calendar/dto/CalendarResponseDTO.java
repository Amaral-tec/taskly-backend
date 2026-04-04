package com.amaral.taskly.calendar.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.amaral.taskly.calendar.enums.CalendarStatus;
import com.amaral.taskly.calendar.enums.RecurrenceType;

public record CalendarResponseDTO(
    Long id,
    UUID publicId,
    String title,
    String description,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    LocalDateTime reminder,
    RecurrenceType recurrenceType,
    CalendarStatus status,
    Long userId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
