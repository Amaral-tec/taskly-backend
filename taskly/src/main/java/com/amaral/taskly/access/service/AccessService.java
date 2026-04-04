package com.amaral.taskly.access.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amaral.taskly.BusinessException;
import com.amaral.taskly.access.dto.AccessRequestDTO;
import com.amaral.taskly.access.dto.AccessResponseDTO;
import com.amaral.taskly.access.model.Access;
import com.amaral.taskly.access.repository.AccessRepository;

@Service
@Transactional
public class AccessService {

    private static final Logger log = LoggerFactory.getLogger(AccessService.class);

    private final AccessRepository accessRepository;

    public AccessService(AccessRepository accessRepository) {
        this.accessRepository = accessRepository;
    }

    public AccessResponseDTO createAccess(AccessRequestDTO dto) {
        accessRepository.findByName(dto.name()).stream()
                .findFirst()
                .ifPresent(a -> {
                    throw new BusinessException("Already registered with the name: " + dto.name());
                });

        Access entity = new Access();
        entity.setName(dto.name());
        accessRepository.save(entity);

        log.info("Record successfully created, id={}", entity.getPublicId());
        return new AccessResponseDTO(entity.getPublicId(), entity.getName());
    }

    public AccessResponseDTO getAccess(UUID publicId) {
        Access entity = findByIdOrThrow(publicId);
        log.info("Record retrieved: id={}", publicId);
        return new AccessResponseDTO(entity.getPublicId(), entity.getName());
    }

    public List<AccessResponseDTO> findAllAccess() {
        List<Access> list = accessRepository.findAll();
        log.info("Retrieving all records, total={}", list.size());

        return list.stream()
                   .map(a -> new AccessResponseDTO(a.getPublicId(), a.getName()))
                   .toList();
    }

    public List<AccessResponseDTO> findAccessByName(String name) {
        List<Access> list = accessRepository.findByName(name);
        log.info("Searching records by name={}, found={}", name, list.size());

        return list.stream()
                   .map(a -> new AccessResponseDTO(a.getPublicId(), a.getName()))
                   .toList();
    }

    public AccessResponseDTO updateAccess(UUID publicId, AccessRequestDTO dto) {
        Access entity = findByIdOrThrow(publicId);

        accessRepository.findByName(dto.name()).stream()
                .filter(a -> !a.getPublicId().equals(publicId))
                .findFirst()
                .ifPresent(a -> {
                    throw new BusinessException("Already registered with the name: " + dto.name());
                });

        entity.setName(dto.name());
        accessRepository.save(entity);

        log.info("Record successfully updated, id={}", entity.getPublicId());
        return new AccessResponseDTO(entity.getPublicId(), entity.getName());
    }

    public void deleteAccess(UUID publicId) {
        Access entity = findByIdOrThrow(publicId);
        entity.setDeleted(true);
        accessRepository.save(entity);
        log.info("Record successfully deleted, id={}", entity.getPublicId());
    }

    public Access findByIdOrThrow(UUID publicId) {
        return accessRepository.findByPublicId(publicId)
                .filter(a -> !a.getDeleted())
                .orElseThrow(() -> new BusinessException("ID not found or deleted: " + publicId));
    }
}
