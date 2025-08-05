package com.codeit.findex.dto.dashboard.response;

import java.time.LocalDate;

public record ChartDataPoint(LocalDate date, double value) {}
