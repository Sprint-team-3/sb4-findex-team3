package com.codeit.findex.domain.intergration.repository;

import com.codeit.findex.domain.base.entity.Integration;
import com.codeit.findex.domain.base.entityEnum.JobType;
import com.codeit.findex.domain.base.entityEnum.Result;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface IntegrationCustomRepository {
  Slice<Integration> searchIntegrations(
      JobType jobType,
      Long indexInfoId,
      LocalDate baseDateFrom,
      LocalDate baseDateTo,
      String worker,
      LocalDateTime jobTimeFrom,
      LocalDateTime jobTimeTo,
      Result status,
      LocalDateTime cursor,
      String sortField,
      String sortDirection,
      Pageable pageable);

  long countIntegrations(
      JobType jobType,
      Long indexInfoId,
      LocalDate baseDateFrom,
      LocalDate baseDateTo,
      String worker,
      LocalDateTime jobTimeFrom,
      LocalDateTime jobTimeTo,
      Result status);
}
