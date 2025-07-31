package com.codeit.findex.dto.integration;

import com.codeit.findex.entityEnum.JobType;
import com.codeit.findex.entityEnum.Result;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SyncJobDto(
    Long id,
    JobType jobType,
    Long indexInfoId,
    LocalDate targetDate,
    String worker,
    LocalDateTime jobTime,
    Result result) {}
