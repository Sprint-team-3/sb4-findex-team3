package com.codeit.findex.mapper;

import com.codeit.findex.dto.integration.SyncJobDto;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entity.Integration;
import com.codeit.findex.entityEnum.JobType;
import com.codeit.findex.entityEnum.Result;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-05T16:37:16+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.15 (Microsoft)"
)
@Component
public class IntegrationMapperImpl implements IntegrationMapper {

    @Override
    public SyncJobDto toSyncJobDto(Integration integration) {
        if ( integration == null ) {
            return null;
        }

        Long id = null;
        JobType jobType = null;
        Long indexInfoId = null;
        LocalDate targetDate = null;
        String worker = null;
        LocalDateTime jobTime = null;
        Result result = null;

        id = integration.getId();
        jobType = integration.getJobType();
        indexInfoId = integrationIndexInfoId( integration );
        targetDate = integration.getBaseDate();
        worker = integration.getWorker();
        jobTime = integration.getJobTime();
        result = integration.getResult();

        SyncJobDto syncJobDto = new SyncJobDto( id, jobType, indexInfoId, targetDate, worker, jobTime, result );

        return syncJobDto;
    }

    private Long integrationIndexInfoId(Integration integration) {
        if ( integration == null ) {
            return null;
        }
        IndexInfo indexInfo = integration.getIndexInfo();
        if ( indexInfo == null ) {
            return null;
        }
        Long id = indexInfo.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
