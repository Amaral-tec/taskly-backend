package com.amaral.taskly.shared.dto;

import java.time.LocalDateTime;

public record ErrorObjectResponseDTO(
    String error,
    String code,
    String path,
    LocalDateTime timestamp
) {}
