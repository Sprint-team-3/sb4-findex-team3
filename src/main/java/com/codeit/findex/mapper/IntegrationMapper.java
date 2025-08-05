package com.codeit.findex.mapper;

import com.codeit.findex.dto.integration.SyncJobDto;
import com.codeit.findex.entity.Integration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IntegrationMapper {

  @Mapping(target = "id", source = "integration.id")
  @Mapping(target = "jobType", source = "integration.jobType")
  @Mapping(target = "indexInfoId", source = "integration.indexInfo.id")
  @Mapping(target = "targetDate", source = "integration.baseDate")
  @Mapping(target = "worker", source = "integration.worker")
  @Mapping(target = "jobTime", source = "integration.jobTime")
  @Mapping(target = "result", source = "integration.result")
  SyncJobDto toSyncJobDto(Integration integration);
}
