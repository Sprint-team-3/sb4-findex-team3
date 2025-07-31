package com.codeit.findex.dto.integration;

import com.codeit.findex.entityEnum.JobType;
import com.codeit.findex.entityEnum.Result;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SyncJobDto(
    UUID id,
    JobType jobType,
    UUID indexInfold,
    LocalDate targetDate,
    String worker,
    Instant jobTime,
    Result result) {}
