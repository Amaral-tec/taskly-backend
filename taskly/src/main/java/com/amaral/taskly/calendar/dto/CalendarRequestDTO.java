package com.amaral.taskly.calendar.dto;

import java.time.LocalDateTime;

import com.amaral.taskly.calendar.enums.CalendarStatus;
import com.amaral.taskly.calendar.enums.RecurrenceType;
import com.amaral.taskly.shared.validation.DateRange;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@DateRange
public record CalendarRequestDTO(

    @NotBlank(message = "Title is required")
    String title,

    String description,

    @NotNull(message = "Start date and time is required")
    @FutureOrPresent(message = "Start date and time must be in the present or future")
    LocalDateTime startDateTime,

    @NotNull(message = "End date and time is required")
    @FutureOrPresent(message = "End date and time must be in the present or future")
    LocalDateTime endDateTime,

    LocalDateTime reminder,

    @NotNull(message = "Recurrence type is required")
    RecurrenceType recurrenceType,

    CalendarStatus status,

    Long userId
) {}
