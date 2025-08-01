package com.codeit.findex.mapper;

import com.codeit.findex.dto.integration.SyncJobDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entity.Integration;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IntegrationMapper {

  @Mapping(target = "id", source = "integrationId")
  @Mapping(expression = "java(com.codeit.findex.entityEnum.JobType.INDEX_INFO)", target = "jobType")
  @Mapping(target = "indexInfoId", source = "indexInfo.id")
  @Mapping(target = "targetDate", source = "indexInfo.basepointInTime")
  @Mapping(target = "worker", source = "worker")
  @Mapping(expression = "java(java.time.LocalDateTime.now())", target = "jobTime")
  @Mapping(expression = "java(com.codeit.findex.entityEnum.Result.SUCCESS)", target = "result")
  SyncJobDto toSyncJobDto(long integrationId, IndexInfo indexInfo, String worker);

  @Mapping(target = "id", source = "integrationId")
  @Mapping(expression = "java(com.codeit.findex.entityEnum.JobType.INDEX_DATA)", target = "jobType")
  @Mapping(target = "indexInfoId", source = "indexData.indexInfo.id")
  @Mapping(target = "targetDate", source = "indexData.baseDate")
  @Mapping(target = "worker", source = "worker")
  @Mapping(expression = "java(java.time.LocalDateTime.now())", target = "jobTime")
  @Mapping(expression = "java(com.codeit.findex.entityEnum.Result.SUCCESS)", target = "result")
  SyncJobDto toSyncJobDto(long integrationId, IndexData indexData, String worker);

  List<SyncJobDto> toSyncJobDtos(List<Integration> integrations);
}
