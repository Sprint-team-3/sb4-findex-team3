package com.codeit.findex.dto.dashboard;

import java.time.LocalDate;
import java.util.Date;
import org.springframework.cglib.core.Local;

public record ChartDataPoint(LocalDate date, double value) {}
