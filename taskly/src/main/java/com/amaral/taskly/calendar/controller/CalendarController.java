package com.amaral.taskly.calendar.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amaral.taskly.calendar.dto.CalendarRequestDTO;
import com.amaral.taskly.calendar.dto.CalendarResponseDTO;
import com.amaral.taskly.calendar.dto.CalendarSearchRequestDTO;
import com.amaral.taskly.calendar.enums.CalendarStatus;
import com.amaral.taskly.calendar.service.CalendarService;
import com.amaral.taskly.user.model.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;  

    @PostMapping
    public ResponseEntity<CalendarResponseDTO> createCalendar(
            @RequestBody @Valid CalendarRequestDTO dto,
            @AuthenticationPrincipal User currentUser) {

        CalendarResponseDTO response = calendarService.createCalendar(dto, currentUser);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<CalendarResponseDTO> getCalendar(@PathVariable UUID publicId) {
        return ResponseEntity.ok(calendarService.getCalendar(publicId));
    }

    @GetMapping
    public ResponseEntity<List<CalendarResponseDTO>> listCalendars(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(calendarService.listCalendars(currentUser));
    }

    @PostMapping("/search")
    public ResponseEntity<List<CalendarResponseDTO>> searchCalendars(
            @RequestBody CalendarSearchRequestDTO filters,
            @AuthenticationPrincipal User currentUser) {

        List<CalendarResponseDTO> calendars = calendarService.searchCalendars(
            filters.title(),
            filters.status(),
            filters.startDate(),
            filters.endDate(),
            currentUser
        );
        return ResponseEntity.ok(calendars);
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<CalendarResponseDTO> updateCalendar(
            @PathVariable UUID publicId,
            @RequestBody @Valid CalendarRequestDTO dto,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(calendarService.updateCalendar(publicId, dto, currentUser));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteCalendar(
            @PathVariable UUID publicId,
            @AuthenticationPrincipal User currentUser) {

        calendarService.deleteCalendar(publicId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{publicId}/{status}")
    public ResponseEntity<CalendarResponseDTO> updateStatus(
            @PathVariable UUID publicId,
            @PathVariable CalendarStatus status,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(calendarService.updateStatus(publicId, status, currentUser));
    }

    @PostMapping("/{publicId}/reminder")
    public ResponseEntity<CalendarResponseDTO> setReminder(
            @PathVariable UUID publicId,
            @RequestParam LocalDateTime reminder,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(calendarService.setReminder(publicId, reminder, currentUser));
    }
}
