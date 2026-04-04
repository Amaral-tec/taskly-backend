package com.amaral.taskly.calendar.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.amaral.taskly.BusinessException;
import com.amaral.taskly.calendar.dto.CalendarRequestDTO;
import com.amaral.taskly.calendar.dto.CalendarResponseDTO;
import com.amaral.taskly.calendar.enums.CalendarStatus;
import com.amaral.taskly.calendar.enums.RecurrenceType;
import com.amaral.taskly.calendar.mapper.CalendarMapper;
import com.amaral.taskly.calendar.model.Calendar;
import com.amaral.taskly.calendar.repository.CalendarRepository;
import com.amaral.taskly.user.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final EntityManager entityManager;

    public CalendarResponseDTO createCalendar(CalendarRequestDTO dto, User user) {
        Calendar calendar = CalendarMapper.toEntity(dto, user);
        calendar.setStatus(CalendarStatus.SCHEDULED);
        calendar.setRecurrenceType(dto.recurrenceType() != null ? dto.recurrenceType() : RecurrenceType.NONE);
        calendarRepository.save(calendar);

        log.info("Calendar created, id={}", calendar.getPublicId());
        return CalendarMapper.toResponseDTO(calendar);
    }

    public CalendarResponseDTO getCalendar(UUID publicId) {
        Calendar calendar = findByIdOrThrow(publicId);
        log.info("Record retrieved: id={}", publicId);
        return CalendarMapper.toResponseDTO(calendar);
    }

    public List<CalendarResponseDTO> listCalendars(User user) {
        return calendarRepository.findByUserAndDeletedFalseOrderByStartDateTimeAsc(user).stream()
                .map(CalendarMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<CalendarResponseDTO> searchCalendars(String title, List<String> status, String startDate, String endDate, User user) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Calendar> cq = cb.createQuery(Calendar.class);
        Root<Calendar> root = cq.from(Calendar.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("user"), user));
        predicates.add(cb.isFalse(root.get("deleted")));

        if (title != null && !title.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (status != null && !status.isEmpty()) {
            List<CalendarStatus> statusEnums = status.stream()
                    .map(s -> {
                        try {
                            return CalendarStatus.valueOf(s.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid status: " + s);
                        }
                    })
                    .toList();
            predicates.add(root.get("status").in(statusEnums));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        try {
            if (startDate != null && !startDate.isBlank()) {
                Date start = sdf.parse(startDate);
                startDateTime = start.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime();
            }
            if (endDate != null && !endDate.isBlank()) {
                Date end = sdf.parse(endDate);
                endDateTime = end.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                                .withHour(23).withMinute(59).withSecond(59);
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }

        if (startDateTime != null && endDateTime != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("startDateTime"), endDateTime));
            predicates.add(cb.greaterThanOrEqualTo(root.get("endDateTime"), startDateTime));
        } else if (startDateTime != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("endDateTime"), startDateTime));
        } else if (endDateTime != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("startDateTime"), endDateTime));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("startDateTime")));

        List<Calendar> calendars = entityManager.createQuery(cq).getResultList();

        return calendars.stream()
                        .map(CalendarMapper::toResponseDTO)
                        .collect(Collectors.toList());
    }

    public CalendarResponseDTO updateCalendar(UUID publicId, CalendarRequestDTO dto, User user) {
        Calendar calendar = findByIdOrThrow(publicId);

        if (!calendar.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot edit this calendar");
        }

        calendar.setTitle(dto.title());
        calendar.setDescription(dto.description());
        calendar.setStartDateTime(dto.startDateTime());
        calendar.setEndDateTime(dto.endDateTime());
        calendar.setReminder(dto.endDateTime());
        calendar.setRecurrenceType(dto.recurrenceType() != null ? dto.recurrenceType() : RecurrenceType.NONE);
        calendar.setStatus(dto.status() != null ? dto.status() : CalendarStatus.SCHEDULED);

        calendarRepository.save(calendar);
        log.info("Calendar updated, id={}", calendar.getPublicId());
        return CalendarMapper.toResponseDTO(calendar);
    }

    public void deleteCalendar(UUID publicId, User user) {
        Calendar calendar = findByIdOrThrow(publicId);

        if (!calendar.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot delete this calendar");
        }

        calendar.setDeleted(true);
        calendarRepository.save(calendar);
        log.info("Calendar soft deleted, id={}", publicId);
    }

    public CalendarResponseDTO updateStatus(UUID publicId, CalendarStatus status, User user) {
        Calendar calendar = findByIdOrThrow(publicId);

        if (!calendar.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot mark this calendar as completed");
        }

        calendar.setStatus(status);
        calendarRepository.save(calendar);
        log.info("Calendar status updated, id={}", publicId);
        return CalendarMapper.toResponseDTO(calendar);
    }

    public CalendarResponseDTO setReminder(UUID publicId, LocalDateTime reminder, User user) {
                                           Calendar calendar = findByIdOrThrow(publicId);
        if (!calendar.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot set reminder for this calendar");
        }

        calendar.setReminder(reminder);
        calendarRepository.save(calendar);
        log.info("Reminder set for calendar, id={}", publicId);
        return CalendarMapper.toResponseDTO(calendar);
    }

    private Calendar findByIdOrThrow(UUID publicId) {
        return calendarRepository.findByPublicId(publicId)
                .filter(a -> !a.getDeleted())
                .orElseThrow(() -> new BusinessException("ID not found or deleted: " + publicId));
    }
}
