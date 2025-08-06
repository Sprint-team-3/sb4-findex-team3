package com.codeit.findex.mapper;

//import com.codeit.findex.dto.dashboard.OpenApiResponseDto;
import com.codeit.findex.dto.indexData.request.IndexDataCreateRequest;
import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.dto.openapi.OpenApiResponseDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {

  @Mapping(target = "indexInfoId", source = "entity.indexInfo.id")
  IndexDataDto toDto(IndexData entity);

  @Mapping(target = "indexInfo.id",source = "dto.indexInfoId")
  IndexData toEntity(IndexDataDto dto);

  IndexData toIndexData(IndexDataDto dto);

  void updateDataFromDto(IndexDataDto dto, @MappingTarget IndexData entity);

  @Mapping(target = "baseDate", source = "item.basDt", qualifiedByName = "stringToLocalDate")
  @Mapping(target = "marketPrice", source = "item.mkp", qualifiedByName = "doubleToDoubleSafe")
  @Mapping(target = "closingPrice", source = "item.clpr", qualifiedByName = "doubleToDoubleSafe")
  @Mapping(target = "highPrice", source = "item.hipr", qualifiedByName = "doubleToDoubleSafe")
  @Mapping(target = "lowPrice", source = "item.lopr", qualifiedByName = "doubleToDoubleSafe")
  @Mapping(target = "versus", source = "item.vs", qualifiedByName = "doubleToDoubleSafe")
  @Mapping(
      target = "fluctuationRate",
      source = "item.fltRt",
      qualifiedByName = "doubleToDoubleSafe")
  @Mapping(target = "tradingQuantity", source = "item.trqu", qualifiedByName = "longToIntSafe")
  @Mapping(target = "tradingPrice", source = "item.trPrc", qualifiedByName = "longToLongSafe")
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
}
