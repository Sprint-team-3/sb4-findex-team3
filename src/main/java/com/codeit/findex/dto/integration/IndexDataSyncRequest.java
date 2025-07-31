package com.codeit.findex.dto.integration;

import java.time.LocalDate;
import java.util.List;

public record IndexDataSyncRequest(
    List<Long> indexInfoIds, LocalDate baseDateFrom, LocalDate baseDateTo) {}
