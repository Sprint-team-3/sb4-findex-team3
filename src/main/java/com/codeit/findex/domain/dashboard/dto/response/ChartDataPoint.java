package com.codeit.findex.domain.dashboard.dto.response;

import java.time.LocalDate;

public record ChartDataPoint(LocalDate date, Double value) {}
