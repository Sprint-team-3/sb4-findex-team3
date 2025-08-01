package com.codeit.findex.service.basic;

import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.mapper.file.IndexDataMapper;
import com.codeit.findex.repository.IndexDataRepository;
import com.codeit.findex.repository.IndexInfoRepository;
import com.codeit.findex.dto.indexData.request.IndexDataDateRequest;
import com.codeit.findex.dto.indexData.request.IndexDataSaveRequest;
import com.codeit.findex.dto.indexData.request.IndexDataSearchRequest;
import com.codeit.findex.dto.indexData.request.IndexDataUpdateRequest;
import com.codeit.findex.service.IndexDataService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicIndexDataService implements IndexDataService {

    private final IndexInfoRepository infoRepository;
    private final IndexDataRepository dataRepository;
    private final IndexDataMapper mapper;

    @Override
    public IndexDataDto registerIndexData(IndexDataSaveRequest request) {
        // 만약, 지수와 날짜의 조합값이 중복된다면 예외를 발생시킨다
        // 중복된다라는 의미는 dataRepository에 지수, 날짜 조합이 이미 존재한다면 이니까 repository와 관련이 있다.
        if(dataRepository.existsByIndexInfoAndBaseDate(request.indexInfo(), request.baseDate())) {
            throw new IllegalArgumentException("지수와 날짜의 조합이 이미 존재해요!");
        }

        // 지수, 날짜부터 상장 시가 총액까지 모든 속성을 입력해 지수 데이터를 등록함
        IndexData indexData = new IndexData();
        indexData.setIndexInfo(request.indexInfo());
        indexData.setBaseDate(request.baseDate());
        indexData.setSourceType(request.sourceType());
        indexData.setOpenPrice(request.openPrice());
        indexData.setClosingPrice(request.closingPrice());
        indexData.setHighPrice(request.highPrice());
        indexData.setLowPrice(request.lowPrice());
        indexData.setChangeValue(request.changeValue());
        indexData.setFluctuationRate(request.fluctuationRate());
        indexData.setTradingVolume(request.tradingVolume());
        indexData.setTradingValue(request.tradingValue());
        indexData.setMarketTotalAmount(request.marketTotalAmount());
        dataRepository.save(indexData); // dataRepository에 indexData 타입으로 저장

        return mapper.toDto(indexData); // 여기에서 Dto로 바꿨다
    }

//    IndexData의 ID를 repository에서 찾은 다음
//    그 ID에 해당하는 IndexData의 지수, 날짜를 제외한 값을 변경한다
    @Override
    public IndexDataDto updateIndexData(IndexDataUpdateRequest request) {
        // 만약 id로 indexData를 못찾으면 예외가 뜸
        IndexData indexData = dataRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("IndexData를 찾을 수 없어요!"));

        indexData.setOpenPrice(request.openPrice());
        indexData.setClosingPrice(request.closingPrice());
        indexData.setHighPrice(request.highPrice());
        indexData.setLowPrice(request.lowPrice());
        indexData.setTradingVolume(request.tradingVolume());
        indexData.setChangeValue(request.changeValue());
        indexData.setFluctuationRate(request.fluctuationRate());

        return mapper.toDto(indexData);
    }

    @Override
    public Page<IndexDataDto> searchByIndexAndDate(IndexDataDateRequest request, Pageable pageable) {
        IndexInfo indexInfo = infoRepository.findById(request.indexInfo()) // infoRepository에서 일단 id를 찾고
                .orElseThrow(() -> new EntityNotFoundException("IndexInfo를 찾을수가 없어요!"));
        // indexInfo와 request를 통해 받은 값들을 입력해서 찾는다
        Page<IndexData> searchData = dataRepository.findByIndexInfoAndBaseDateBetween(indexInfo, request.startDate(), request.endDate(), pageable);
        return searchData.map(mapper::toDto); // map을 통한 형변환, Page<IndexDataDto>으로 변환한다
    }

    @Override
    public IndexDataDto searchIndexData(IndexDataSearchRequest request) {
        IndexData indexData = null;
        if(dataRepository.findById(request.id()).equals(infoRepository.findById(request.id()))) {
            indexData = dataRepository.findById(request.id()).get();
        }
        return mapper.toDto(indexData);
    }

    @Override
    public void deleteIndexData(long id) {
        // dataRepository에서 id에 맞는 IndexData가 없다면 예외를 발생시킨다
        IndexData indexData = dataRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("IndexData를 찾을 수 없어요!"));

        dataRepository.deleteById(id);
    };
}