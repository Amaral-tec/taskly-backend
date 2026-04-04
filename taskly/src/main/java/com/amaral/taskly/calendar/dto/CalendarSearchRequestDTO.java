package com.amaral.taskly.calendar.dto;

import java.util.List;

public record CalendarSearchRequestDTO(
    String title,
    List<String> status,
    String startDate,
    String endDate
) {}
