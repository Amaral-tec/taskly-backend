package com.amaral.taskly.access.dto;

import jakarta.validation.constraints.NotBlank;

public record AccessRequestDTO(
    @NotBlank(message = "Name is required") String name
) {}
