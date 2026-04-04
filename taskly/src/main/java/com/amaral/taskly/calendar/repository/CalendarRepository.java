package com.amaral.taskly.calendar.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaral.taskly.calendar.model.Calendar;
import com.amaral.taskly.user.model.User;

import jakarta.transaction.Transactional;

@Transactional
public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    List<Calendar> findByUserAndDeletedFalseOrderByStartDateTimeAsc(User user);

    Optional<Calendar> findByPublicId(UUID publicId);
}
