package com.codeit.findex.mapper;

import com.codeit.findex.dto.IndexDataDto;
import com.codeit.findex.dto.dashboard.OpenApiResponseDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {

  IndexData toIndexData(IndexDataDto dto);

  IndexDataDto toIndexDataDto(IndexData dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateDataFromDto(IndexDataDto dto, @MappingTarget IndexData entity);

  @Mapping(target = "indexInfo", source = "indexInfo")
  @Mapping(target = "baseDate", source = "item.basDt", qualifiedByName = "stringToLocalDate")
  @Mapping(target = "openPrice", source = "item.mkp", qualifiedByName = "doubleToDoubleSafe")
  @Mapping(target = "closingPrice", source = "item.clpr", qualifiedByName = "doubleToDoubleSafe")
  @Mapping(target = "highPrice", source = "item.hipr", qualifiedByName = "doubleToDoubleSafe")
  @Mapping(target = "lowPrice", source = "item.lopr", qualifiedByName = "doubleToDoubleSafe")
  @Mapping(target = "changeValue", source = "item.vs", qualifiedByName = "doubleToDoubleSafe")
  @Mapping(
      target = "fluctuationRate",
      source = "item.fltRt",
      qualifiedByName = "doubleToDoubleSafe")
  @Mapping(target = "tradingVolume", source = "item.trqu", qualifiedByName = "longToIntSafe")
  @Mapping(target = "tradingValue", source = "item.trPrc", qualifiedByName = "longToLongSafe")
  @Mapping(
      target = "marketTotalAmount",
      source = "item.lstgMrktTotAmt",
      qualifiedByName = "longToLongSafe")
  IndexData toIndexData(IndexInfo indexInfo, OpenApiResponseDto.IndexItemDto item);

  // ------------------ @Named 헬퍼 메소드 ------------------

  @Named("stringToLocalDate")
  default java.time.LocalDate stringToLocalDate(String basDt) {
    java.time.format.DateTimeFormatter fmt =
        java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
    return java.time.LocalDate.parse(java.util.Objects.requireNonNull(basDt), fmt);
  }

  @Named("doubleToDoubleSafe")
  default double doubleToDoubleSafe(Double d) {
    return d != null ? d : 0.0;
  }

  @Named("longToIntSafe")
  default int longToIntSafe(Long l) {
    return l != null ? l.intValue() : 0;
  }

  @Named("longToLongSafe")
  default long longToLongSafe(Long l) {
    return l != null ? l : 0L;
  }

  // ------------------ 공용 변환 유틸 ------------------

  default Instant parseBaseDateTime(String basDt, String basPntm) {
    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd");
    ZoneId zone = ZoneId.of("Asia/Seoul");
    LocalDate date = LocalDate.parse(Objects.requireNonNull(basDt), dateFmt);
    LocalTime time = parseBasePointInTime(basPntm);
    return date.atTime(time).atZone(zone).toInstant();
  }

  default LocalTime parseBasePointInTime(String basPntm) {
    if (basPntm == null || basPntm.isBlank()) {
      return LocalTime.MIDNIGHT;
    }
    String padded = basPntm.length() % 2 == 1 ? "0" + basPntm : basPntm;
    try {
      int hour = Integer.parseInt(padded.substring(0, 2));
      int minute = Integer.parseInt(padded.substring(2));
      return LocalTime.of(hour, minute);
    } catch (Exception e) {
      return LocalTime.MIDNIGHT;
    }
  }

  default Timestamp toTimestamp(Instant instant) {
    return instant != null ? Timestamp.from(instant) : null;
  }
}
