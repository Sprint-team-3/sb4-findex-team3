package com.codeit.findex.domain.intergration.dto;

import com.codeit.findex.domain.base.entityEnum.JobType;
import com.codeit.findex.domain.base.entityEnum.Result;
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
