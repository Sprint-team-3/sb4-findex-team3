package com.codeit.findex.mapper;

import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entityEnum.SourceType;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-05T11:06:11+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class IndexDataMapperImpl implements IndexDataMapper {

    @Override
    public IndexDataDto toDto(IndexData entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        LocalDate baseDate = null;
        SourceType sourceType = null;
        double closingPrice = 0.0d;
        double highPrice = 0.0d;
        double lowPrice = 0.0d;
        double fluctuationRate = 0.0d;
        long marketTotalAmount = 0L;

        id = entity.getId();
        baseDate = entity.getBaseDate();
        sourceType = entity.getSourceType();
        closingPrice = entity.getClosingPrice();
        highPrice = entity.getHighPrice();
        lowPrice = entity.getLowPrice();
        fluctuationRate = entity.getFluctuationRate();
        marketTotalAmount = entity.getMarketTotalAmount();

        Long indexInfoId = null;
        double marketPrice = 0.0d;
        double versus = 0.0d;
        long tradingQuantity = 0L;
        long tradingPrice = 0L;

        IndexDataDto indexDataDto = new IndexDataDto( id, indexInfoId, baseDate, sourceType, marketPrice, closingPrice, highPrice, lowPrice, versus, fluctuationRate, tradingQuantity, tradingPrice, marketTotalAmount );

        return indexDataDto;
    }

    @Override
    public IndexData toEntity(IndexDataDto dto) {
        if ( dto == null ) {
            return null;
        }

        IndexData indexData = new IndexData();

        indexData.setBaseDate( dto.baseDate() );
        indexData.setSourceType( dto.sourceType() );
        indexData.setClosingPrice( dto.closingPrice() );
        indexData.setHighPrice( dto.highPrice() );
        indexData.setLowPrice( dto.lowPrice() );
        indexData.setFluctuationRate( dto.fluctuationRate() );
        indexData.setMarketTotalAmount( dto.marketTotalAmount() );

        return indexData;
    }

    @Override
    public IndexData toIndexData(IndexDataDto dto) {
        if ( dto == null ) {
            return null;
        }

        IndexData indexData = new IndexData();

        indexData.setBaseDate( dto.baseDate() );
        indexData.setSourceType( dto.sourceType() );
        indexData.setClosingPrice( dto.closingPrice() );
        indexData.setHighPrice( dto.highPrice() );
        indexData.setLowPrice( dto.lowPrice() );
        indexData.setFluctuationRate( dto.fluctuationRate() );
        indexData.setMarketTotalAmount( dto.marketTotalAmount() );

        return indexData;
    }

    @Override
    public void updateDataFromDto(IndexDataDto dto, IndexData entity) {
        if ( dto == null ) {
            return;
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
        entity.setMarketTotalAmount( dto.marketTotalAmount() );
    }
}
