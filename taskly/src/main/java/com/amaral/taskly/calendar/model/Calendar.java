package com.amaral.taskly.calendar.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import com.amaral.taskly.calendar.enums.CalendarStatus;
import com.amaral.taskly.calendar.enums.RecurrenceType;
import com.amaral.taskly.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLRestriction("deleted = false")
@Table(name = "calendars")
@SequenceGenerator(name = "seq_calendar", sequenceName = "seq_calendar", initialValue = 1, allocationSize = 1)
public class Calendar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_calendar")
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId = UUID.randomUUID();

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Start date and time are required")
    @FutureOrPresent(message = "Start date and time must be present or future")
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Future(message = "End date and time must be in the future")
    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @FutureOrPresent(message = "Reminder must be present or future")
    @Column(name = "reminder")
    private LocalDateTime reminder;

    @NotNull(message = "Recurrence type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_type", nullable = false)
    private RecurrenceType recurrenceType = RecurrenceType.NONE;

    @Positive(message = "Recurrence interval must be greater than zero")
    @Column(name = "recurrence_interval")
    private Integer recurrenceInterval = 1;

    @Future(message = "Recurrence end date must be in the future")
    @Column(name = "recurrence_end_date")
    private LocalDateTime recurrenceEndDate;

    @Size(max = 255, message = "Location cannot exceed 255 characters")
    private String location;

    @Size(max = 255, message = "Meeting link cannot exceed 255 characters")
    private String meetingLink;

    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be in HEX format (e.g., #FFFFFF)")
    private String color;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private CalendarStatus status = CalendarStatus.SCHEDULED;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotNull
    private Boolean deleted = Boolean.FALSE;
}
