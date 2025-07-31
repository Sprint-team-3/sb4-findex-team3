package com.codeit.findex.dto.integration;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record IndexDataSyncRequest(
    List<UUID> indexInfolds, LocalDate baseDateFrom, LocalDate baseDateTo) {}
