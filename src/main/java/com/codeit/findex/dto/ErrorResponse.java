package com.codeit.findex.dto;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp, int status, String message, String details) {}
