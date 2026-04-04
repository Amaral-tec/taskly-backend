package com.amaral.taskly.access.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amaral.taskly.access.dto.AccessRequestDTO;
import com.amaral.taskly.access.dto.AccessResponseDTO;
import com.amaral.taskly.access.service.AccessService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
public class AccessController {

    private final AccessService accessService;

    @PostMapping
    public ResponseEntity<AccessResponseDTO> createAccess(@RequestBody @Valid AccessRequestDTO dto) {
        AccessResponseDTO response = accessService.createAccess(dto);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<AccessResponseDTO> getAccess(@PathVariable UUID publicId) {
        AccessResponseDTO response = accessService.getAccess(publicId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AccessResponseDTO>> findAllAccess() {
        return ResponseEntity.ok(accessService.findAllAccess());
    }

    @GetMapping("/search")
    public ResponseEntity<List<AccessResponseDTO>> findAccessByName(@RequestParam String name) {
        return ResponseEntity.ok(accessService.findAccessByName(name));
    }

    @PutMapping("/{publicId}")
    public ResponseEntity<AccessResponseDTO> updateAccess(@PathVariable UUID publicId,
                                                          @RequestBody @Valid AccessRequestDTO dto) {
        return ResponseEntity.ok(accessService.updateAccess(publicId, dto));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteAccessById(@PathVariable UUID publicId) {
        accessService.deleteAccess(publicId);
        return ResponseEntity.noContent().build();
    }
}
