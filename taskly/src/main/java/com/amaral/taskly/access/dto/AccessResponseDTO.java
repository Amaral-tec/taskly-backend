package com.amaral.taskly.access.dto;

import java.util.UUID;

public record AccessResponseDTO(
    UUID publicId,
    String name
) {}
