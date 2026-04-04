package com.amaral.taskly.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.amaral.taskly.BusinessException;
import com.amaral.taskly.calendar.dto.CalendarRequestDTO;
import com.amaral.taskly.calendar.dto.CalendarResponseDTO;
import com.amaral.taskly.calendar.enums.CalendarStatus;
import com.amaral.taskly.calendar.enums.RecurrenceType;
import com.amaral.taskly.calendar.model.Calendar;
import com.amaral.taskly.calendar.repository.CalendarRepository;
import com.amaral.taskly.calendar.service.CalendarService;
import com.amaral.taskly.user.model.User;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private jakarta.persistence.EntityManager entityManager;

    @InjectMocks
    private CalendarService calendarService;

    private UUID id;
    private Calendar calendar;
    private User user;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        user = new User();
        user.setId(1L);
        user.setPublicId(id);
        user.setLogin("test@email.com");

        calendar = new Calendar();
        calendar.setId(10L);
        calendar.setPublicId(id);
        calendar.setTitle("Meeting");
        calendar.setUser(user);
        calendar.setDeleted(false);
        calendar.setStatus(CalendarStatus.SCHEDULED);
        calendar.setRecurrenceType(RecurrenceType.NONE);
        calendar.setStartDateTime(LocalDateTime.now());
        calendar.setEndDateTime(LocalDateTime.now().plusHours(1));
    }

    @Test
    void shouldCreateCalendarSuccessfully() {
        // given
        CalendarRequestDTO dto = new CalendarRequestDTO(
                "Meeting", "Team sync", LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().minusHours(1), RecurrenceType.NONE,
                CalendarStatus.SCHEDULED, user.getId()
        );

        given(calendarRepository.save(any(Calendar.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        CalendarResponseDTO response = calendarService.createCalendar(dto, user);

        // then
        assertThat(response.title()).isEqualTo("Meeting");
        assertThat(response.status()).isEqualTo(CalendarStatus.SCHEDULED);
        assertThat(response.recurrenceType()).isEqualTo(RecurrenceType.NONE);
        then(calendarRepository).should().save(any(Calendar.class));
    }

    @Test
    void shouldSearchCalendarsSuccessfully() {
        // given
        String title = "meeting";
        String status = "SCHEDULED";
        String startDate = LocalDateTime.now().minusDays(1).toString();
        String endDate = LocalDateTime.now().plusDays(1).toString();

        Calendar anotherCalendar = new Calendar();
        anotherCalendar.setId(20L);
        anotherCalendar.setPublicId(UUID.randomUUID());
        anotherCalendar.setTitle("Team Meeting");
        anotherCalendar.setUser(user);
        anotherCalendar.setDeleted(false);
        anotherCalendar.setStatus(CalendarStatus.SCHEDULED);
        anotherCalendar.setStartDateTime(LocalDateTime.now());
        anotherCalendar.setEndDateTime(LocalDateTime.now().plusHours(2));

        // mocks para Criteria API
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Calendar> cq = mock(CriteriaQuery.class);
        Root<Calendar> root = mock(Root.class);
        TypedQuery<Calendar> query = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Calendar.class)).thenReturn(cq);
        when(cq.from(Calendar.class)).thenReturn(root);
        when(entityManager.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(anotherCalendar));

        // when
        List<String> statuses = List.of("SCHEDULED");
        List<CalendarResponseDTO> result = calendarService.searchCalendars(
                title, statuses, startDate, endDate, user
        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Team Meeting");

        verify(entityManager).createQuery(cq);
        verify(query).getResultList();
    }


    @Test
    void shouldUpdateCalendarSuccessfully() {
        // given
        CalendarRequestDTO dto = new CalendarRequestDTO(
                "Update test", 
                "Calendar update test", 
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1), 
                LocalDateTime.now().minusHours(1), 
                RecurrenceType.NONE,
                CalendarStatus.SCHEDULED, 
                user.getId()
        );
        when(calendarRepository.findByPublicId(id)).thenReturn(Optional.of(calendar));

        // when
        CalendarResponseDTO response = calendarService.updateCalendar(id, dto, user);

        // then
        assertThat(response.title()).isEqualTo("Update test");
        assertThat(response.description()).isEqualTo("Calendar update test");
        assertThat(response.status()).isEqualTo(CalendarStatus.SCHEDULED);
        assertThat(response.recurrenceType()).isEqualTo(RecurrenceType.NONE);

        verify(calendarRepository).save(calendar);
    }

    @Test
    void shouldReturnCalendarById() {
        // given
        given(calendarRepository.findByPublicId(id)).willReturn(Optional.of(calendar));

        // when
        CalendarResponseDTO response = calendarService.getCalendar(id);

        // then
        assertThat(response.publicId()).isEqualTo(id);
        assertThat(response.title()).isEqualTo("Meeting");
        then(calendarRepository).should().findByPublicId(id);
    }

    @Test
    void shouldThrowExceptionWhenIdNotFound() {
        // given
        given(calendarRepository.findByPublicId(id)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> calendarService.getCalendar(id))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ID not found or deleted");

        then(calendarRepository).should().findByPublicId(id);
    }

    @Test
    void shouldListCalendarsForUser() {
        // given
        given(calendarRepository.findByUserAndDeletedFalseOrderByStartDateTimeAsc(user))
                .willReturn(List.of(calendar));

        // when
        var result = calendarService.listCalendars(user);

        // then
        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(10L);
                    assertThat(dto.title()).isEqualTo("Meeting");
                });

        then(calendarRepository).should().findByUserAndDeletedFalseOrderByStartDateTimeAsc(user);
    }

    @Test
    void shouldDeleteCalendarSuccessfully() {
        // given
        given(calendarRepository.findByPublicId(id)).willReturn(Optional.of(calendar));

        // when
        calendarService.deleteCalendar(id, user);

        // then
        ArgumentCaptor<Calendar> captor = ArgumentCaptor.forClass(Calendar.class);
        then(calendarRepository).should().save(captor.capture());
        assertThat(captor.getValue().getDeleted()).isTrue();
    }
}
