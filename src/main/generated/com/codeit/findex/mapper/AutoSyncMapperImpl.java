package com.codeit.findex.mapper;

import com.codeit.findex.dto.autosync.response.AutoSyncConfigDto;
import com.codeit.findex.entity.IndexInfo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-05T14:37:18+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class AutoSyncMapperImpl implements AutoSyncMapper {

    @Override
    public AutoSyncConfigDto toAutoSyncConfigDto(IndexInfo indexInfo) {
        if ( indexInfo == null ) {
            return null;
        }

        long indexInfoId = 0L;
        String indexClassification = null;
        String indexName = null;
        long id = 0L;
        boolean enabled = false;

        if ( indexInfo.getId() != null ) {
            indexInfoId = indexInfo.getId();
        }
        indexClassification = indexInfo.getIndexClassification();
        indexName = indexInfo.getIndexName();
        if ( indexInfo.getId() != null ) {
            id = indexInfo.getId();
        }
        enabled = indexInfo.isEnabled();

        AutoSyncConfigDto autoSyncConfigDto = new AutoSyncConfigDto( id, indexInfoId, indexClassification, indexName, enabled );

        return autoSyncConfigDto;
    }

    @Override
    public List<AutoSyncConfigDto> toAutoSyncConfigDtoList(List<IndexInfo> indexInfoList) {
        if ( indexInfoList == null ) {
            return null;
        }

        List<AutoSyncConfigDto> list = new ArrayList<AutoSyncConfigDto>( indexInfoList.size() );
        for ( IndexInfo indexInfo : indexInfoList ) {
            list.add( toAutoSyncConfigDto( indexInfo ) );
        }

        return list;
    }
}
