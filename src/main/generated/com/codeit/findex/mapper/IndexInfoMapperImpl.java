package com.codeit.findex.mapper;

import com.codeit.findex.dto.indexInfo.request.IndexInfoCreateRequest;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoSummaryDto;
import com.codeit.findex.entity.IndexInfo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-05T14:37:22+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class IndexInfoMapperImpl implements IndexInfoMapper {

    @Override
    public IndexInfo toIndexInfo(IndexInfoDto dto) {
        if ( dto == null ) {
            return null;
        }

        IndexInfo indexInfo = new IndexInfo();

        indexInfo.setIndexClassification( dto.getIndexClassification() );
        indexInfo.setIndexName( dto.getIndexName() );
        indexInfo.setEmployedItemsCount( dto.getEmployedItemsCount() );
        indexInfo.setBasePointInTime( dto.getBasePointInTime() );
        indexInfo.setBaseIndex( dto.getBaseIndex() );
        indexInfo.setSourceType( dto.getSourceType() );
        indexInfo.setFavorite( dto.getFavorite() );

        return indexInfo;
    }

    @Override
    public IndexInfoDto toIndexInfoDto(IndexInfo entity) {
        if ( entity == null ) {
            return null;
        }

        IndexInfoDto indexInfoDto = new IndexInfoDto();

        indexInfoDto.setId( entity.getId() );
        indexInfoDto.setIndexClassification( entity.getIndexClassification() );
        indexInfoDto.setIndexName( entity.getIndexName() );
        indexInfoDto.setEmployedItemsCount( entity.getEmployedItemsCount() );
        indexInfoDto.setBasePointInTime( entity.getBasePointInTime() );
        indexInfoDto.setBaseIndex( entity.getBaseIndex() );
        indexInfoDto.setSourceType( entity.getSourceType() );
        indexInfoDto.setFavorite( entity.getFavorite() );

        return indexInfoDto;
    }

    @Override
    public List<IndexInfoDto> toIndexInfoDtoList(List<IndexInfo> indexInfos) {
        if ( indexInfos == null ) {
            return null;
        }

        List<IndexInfoDto> list = new ArrayList<IndexInfoDto>( indexInfos.size() );
        for ( IndexInfo indexInfo : indexInfos ) {
            list.add( toIndexInfoDto( indexInfo ) );
        }

        return list;
    }

    @Override
    public IndexInfoSummaryDto toIndexInfoSummaryDto(IndexInfo indexInfo) {
        if ( indexInfo == null ) {
            return null;
        }

        IndexInfoSummaryDto indexInfoSummaryDto = new IndexInfoSummaryDto();

        if ( indexInfo.getId() != null ) {
            indexInfoSummaryDto.setId( indexInfo.getId() );
        }
        indexInfoSummaryDto.setIndexClassification( indexInfo.getIndexClassification() );
        indexInfoSummaryDto.setIndexName( indexInfo.getIndexName() );

        return indexInfoSummaryDto;
    }

    @Override
    public IndexInfo IndexInfoCreateDtoToIndexInfo(IndexInfoCreateRequest dto) {
        if ( dto == null ) {
            return null;
        }

        IndexInfo indexInfo = new IndexInfo();

        indexInfo.setIndexClassification( dto.getIndexClassification() );
        indexInfo.setIndexName( dto.getIndexName() );
        indexInfo.setEmployedItemsCount( dto.getEmployedItemsCount() );
        indexInfo.setBasePointInTime( dto.getBasePointInTime() );
        indexInfo.setBaseIndex( dto.getBaseIndex() );
        indexInfo.setFavorite( dto.getFavorite() );

        indexInfo.setSourceType( com.codeit.findex.entityEnum.SourceType.USER );

        return indexInfo;
    }

    @Override
    public void updateInfoFromDto(IndexInfoDto dto, IndexInfo entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getIndexClassification() != null ) {
            entity.setIndexClassification( dto.getIndexClassification() );
        }
        if ( dto.getIndexName() != null ) {
            entity.setIndexName( dto.getIndexName() );
        }
        entity.setEmployedItemsCount( dto.getEmployedItemsCount() );
        if ( dto.getBasePointInTime() != null ) {
            entity.setBasePointInTime( dto.getBasePointInTime() );
        }
        entity.setBaseIndex( dto.getBaseIndex() );
        if ( dto.getSourceType() != null ) {
            entity.setSourceType( dto.getSourceType() );
        }
        if ( dto.getFavorite() != null ) {
            entity.setFavorite( dto.getFavorite() );
        }
    }
}
