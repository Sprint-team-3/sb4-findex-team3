package com.codeit.findex.mapper;

import com.codeit.findex.dto.dashboard.OpenApiResponseDto;
import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entityEnum.SourceType;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-05T21:49:01+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class IndexDataMapperImpl implements IndexDataMapper {

    @Override
    public IndexDataDto toDto(IndexData entity) {
        if ( entity == null ) {
            return null;
        }

        Long tradingPrice = null;
        Long tradingQuantity = null;
        Long indexInfoId = null;
        Long id = null;
        LocalDate baseDate = null;
        SourceType sourceType = null;
        double closingPrice = 0.0d;
        double highPrice = 0.0d;
        double lowPrice = 0.0d;
        double fluctuationRate = 0.0d;
        Long marketTotalAmount = null;

        tradingPrice = (long) entity.getTradingVolume();
        tradingQuantity = entity.getTradingValue();
        indexInfoId = entityIndexInfoId( entity );
        id = entity.getId();
        baseDate = entity.getBaseDate();
        sourceType = entity.getSourceType();
        closingPrice = entity.getClosingPrice();
        highPrice = entity.getHighPrice();
        lowPrice = entity.getLowPrice();
        fluctuationRate = entity.getFluctuationRate();
        marketTotalAmount = entity.getMarketTotalAmount();

        double marketPrice = 0.0d;
        double versus = 0.0d;

        IndexDataDto indexDataDto = new IndexDataDto( id, indexInfoId, baseDate, sourceType, marketPrice, closingPrice, highPrice, lowPrice, versus, fluctuationRate, tradingQuantity, tradingPrice, marketTotalAmount );

        return indexDataDto;
    }

    @Override
    public IndexData toEntity(IndexDataDto dto) {
        if ( dto == null ) {
            return null;
        }

        IndexData indexData = new IndexData();

        indexData.setIndexInfo( indexDataDtoToIndexInfo( dto ) );
        if ( dto.tradingPrice() != null ) {
            indexData.setTradingVolume( dto.tradingPrice().intValue() );
        }
        if ( dto.tradingQuantity() != null ) {
            indexData.setTradingValue( dto.tradingQuantity() );
        }
        indexData.setId( dto.id() );
        indexData.setBaseDate( dto.baseDate() );
        indexData.setSourceType( dto.sourceType() );
        indexData.setClosingPrice( dto.closingPrice() );
        indexData.setHighPrice( dto.highPrice() );
        indexData.setLowPrice( dto.lowPrice() );
        indexData.setFluctuationRate( dto.fluctuationRate() );
        if ( dto.marketTotalAmount() != null ) {
            indexData.setMarketTotalAmount( dto.marketTotalAmount() );
        }

        return indexData;
    }

    @Override
    public IndexData toIndexData(IndexDataDto dto) {
        if ( dto == null ) {
            return null;
        }

        IndexData indexData = new IndexData();

        indexData.setId( dto.id() );
        indexData.setBaseDate( dto.baseDate() );
        indexData.setSourceType( dto.sourceType() );
        indexData.setClosingPrice( dto.closingPrice() );
        indexData.setHighPrice( dto.highPrice() );
        indexData.setLowPrice( dto.lowPrice() );
        indexData.setFluctuationRate( dto.fluctuationRate() );
        if ( dto.marketTotalAmount() != null ) {
            indexData.setMarketTotalAmount( dto.marketTotalAmount() );
        }

        return indexData;
    }

    @Override
    public void updateDataFromDto(IndexDataDto dto, IndexData entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.id() != null ) {
            entity.setId( dto.id() );
        }
        if ( dto.baseDate() != null ) {
            entity.setBaseDate( dto.baseDate() );
        }
        if ( dto.sourceType() != null ) {
            entity.setSourceType( dto.sourceType() );
        }
        entity.setClosingPrice( dto.closingPrice() );
        entity.setHighPrice( dto.highPrice() );
        entity.setLowPrice( dto.lowPrice() );
        entity.setFluctuationRate( dto.fluctuationRate() );
        if ( dto.marketTotalAmount() != null ) {
            entity.setMarketTotalAmount( dto.marketTotalAmount() );
        }
    }

    @Override
    public IndexData toIndexData(IndexInfo indexInfo, OpenApiResponseDto.IndexItemDto item) {
        if ( indexInfo == null && item == null ) {
            return null;
        }

        IndexData indexData = new IndexData();

        if ( indexInfo != null ) {
            indexData.setId( indexInfo.getId() );
            indexData.setCreatedAt( indexInfo.getCreatedAt() );
            indexData.setUpdatedAt( indexInfo.getUpdatedAt() );
            indexData.setSourceType( indexInfo.getSourceType() );
            indexData.setEnabled( indexInfo.isEnabled() );
        }
        if ( item != null ) {
            indexData.setBaseDate( stringToLocalDate( item.basDt() ) );
            indexData.setOpenPrice( doubleToDoubleSafe( item.mkp() ) );
            indexData.setClosingPrice( doubleToDoubleSafe( item.clpr() ) );
            indexData.setHighPrice( doubleToDoubleSafe( item.hipr() ) );
            indexData.setLowPrice( doubleToDoubleSafe( item.lopr() ) );
            indexData.setChangeValue( doubleToDoubleSafe( item.vs() ) );
            indexData.setFluctuationRate( doubleToDoubleSafe( item.fltRt() ) );
            indexData.setTradingVolume( longToIntSafe( item.trqu() ) );
            indexData.setTradingValue( longToLongSafe( item.trPrc() ) );
            indexData.setMarketTotalAmount( longToLongSafe( item.lstgMrktTotAmt() ) );
        }

        return indexData;
    }

    private Long entityIndexInfoId(IndexData indexData) {
        if ( indexData == null ) {
            return null;
        }
        IndexInfo indexInfo = indexData.getIndexInfo();
        if ( indexInfo == null ) {
            return null;
        }
        Long id = indexInfo.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected IndexInfo indexDataDtoToIndexInfo(IndexDataDto indexDataDto) {
        if ( indexDataDto == null ) {
            return null;
        }

        IndexInfo indexInfo = new IndexInfo();

        indexInfo.setId( indexDataDto.indexInfoId() );

        return indexInfo;
    }
}
