package com.amaral.taskly.unit.service;

import com.amaral.taskly.BusinessException;
import com.amaral.taskly.access.dto.AccessRequestDTO;
import com.amaral.taskly.access.dto.AccessResponseDTO;
import com.amaral.taskly.access.model.Access;
import com.amaral.taskly.access.repository.AccessRepository;
import com.amaral.taskly.access.service.AccessService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessServiceTest {

    @Mock
    private AccessRepository accessRepository;

    @InjectMocks
    private AccessService accessService;

    private UUID id;
    private Access access;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        access = new Access();
        access.setPublicId(id);
        access.setName("ADMIN");
        access.setDeleted(false);
    }

    @Test
    void shouldCreateAccessSuccessfully() {
        // given
        AccessRequestDTO dto = new AccessRequestDTO("USER");
        when(accessRepository.findByName("USER")).thenReturn(List.of());

        // when
        AccessResponseDTO response = accessService.createAccess(dto);

        // then
        assertThat(response.name()).isEqualTo("USER");
        verify(accessRepository).save(any(Access.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateAccess() {
        // given
        AccessRequestDTO dto = new AccessRequestDTO("ADMIN");
        when(accessRepository.findByName("ADMIN")).thenReturn(List.of(access));

        // when/then
        assertThatThrownBy(() -> accessService.createAccess(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Already registered with the name: ADMIN");
    }

    @Test
    void shouldReturnAccessById() {
        // given
        when(accessRepository.findByPublicId(id)).thenReturn(Optional.of(access));

        // when
        AccessResponseDTO response = accessService.getAccess(id);

        // then
        assertThat(response.publicId()).isEqualTo(id);
        assertThat(response.name()).isEqualTo("ADMIN");
    }

    @Test
    void shouldThrowExceptionWhenIdNotFound() {
        // given
        when(accessRepository.findByPublicId(id)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> accessService.getAccess(id))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ID not found or deleted");
    }

    @Test
    void shouldFindAllAccess() {
        // given
        when(accessRepository.findAll()).thenReturn(List.of(access));

        // when
        var list = accessService.findAllAccess();

        // then
        assertThat(list).hasSize(1);
        assertThat(list.get(0).name()).isEqualTo("ADMIN");
    }

    @Test
    void shouldUpdateAccessSuccessfully() {
        // given
        AccessRequestDTO dto = new AccessRequestDTO("MANAGER");
        when(accessRepository.findByPublicId(id)).thenReturn(Optional.of(access));
        when(accessRepository.findByName("MANAGER")).thenReturn(List.of());

        // when
        AccessResponseDTO response = accessService.updateAccess(id, dto);

        // then
        assertThat(response.name()).isEqualTo("MANAGER");
        verify(accessRepository).save(access);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithDuplicateName() {
        // given
        Access duplicate = new Access();
        duplicate.setPublicId(UUID.randomUUID());
        duplicate.setName("DUPLICATE");
        duplicate.setDeleted(false);

        AccessRequestDTO dto = new AccessRequestDTO("DUPLICATE");
        when(accessRepository.findByPublicId(id)).thenReturn(Optional.of(access));
        when(accessRepository.findByName("DUPLICATE")).thenReturn(List.of(duplicate));

        // when/then
        assertThatThrownBy(() -> accessService.updateAccess(id, dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Already registered with the name: DUPLICATE");
    }

    @Test
    void shouldDeleteAccessSuccessfully() {
        // given
        when(accessRepository.findByPublicId(id)).thenReturn(Optional.of(access));

        // when
        accessService.deleteAccess(id);

        // then
        ArgumentCaptor<Access> captor = ArgumentCaptor.forClass(Access.class);
        verify(accessRepository).save(captor.capture());
        assertThat(captor.getValue().getDeleted()).isTrue();
    }
}