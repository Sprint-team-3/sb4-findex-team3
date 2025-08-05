package com.codeit.findex.service.basic;

import com.codeit.findex.dto.indexData.request.*;
import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entityEnum.SourceType;
import com.codeit.findex.mapper.CSVStringMapper;
import com.codeit.findex.mapper.IndexDataMapper;
import com.codeit.findex.repository.IndexDataRepository;
import com.codeit.findex.repository.IndexInfoRepository;
import com.codeit.findex.service.IndexDataService;
import com.opencsv.CSVWriter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BasicIndexDataService implements IndexDataService {

    private final IndexInfoRepository infoRepository;
    private final IndexDataRepository dataRepository;
    private final IndexDataMapper mapper;

    @Override
    public IndexDataDto registerIndexData(IndexDataCreateRequest request) { // 지수 데이터 등록
        // 만약, 지수와 날짜의 조합값이 중복된다면 예외를 발생시킨다
        // 중복된다라는 의미는 dataRepository에 지수, 날짜 조합이 이미 존재한다면 이니까 repository와 관련이 있다.
        if(dataRepository.existsByIndexInfoIdAndBaseDate(request.indexInfoId(), request.baseDate())) {
            throw new IllegalArgumentException("Index and Date already exists!");
        }
        // 만약, IndexDataSaveRequest로부터 받은 Long타입 indexInfoId가 존재하지 않는다면, 예외발생
        IndexInfo indexInfo = infoRepository.findById(request.indexInfoId())
                .orElseThrow(() -> new EntityNotFoundException("IndexInfo not found!"));

//        // IndexData 객체를 생성한다
//        IndexData indexData = new IndexData();
//
//        // indexData에 데이터를 넣는다
//        indexData.setIndexInfo(indexInfo);
//        indexData.setBaseDate(request.baseDate());
//        indexData.setSourceType(SourceType.USER);
//        indexData.setOpenPrice(request.openPrice());
//        indexData.setClosingPrice(request.closingPrice());
//        indexData.setHighPrice(request.highPrice());
//        indexData.setLowPrice(request.lowPrice());
//        indexData.setChangeValue(request.changeValue());
//        indexData.setFluctuationRate(request.fluctuationRate());
//        indexData.setTradingVolume(request.tradingVolume());
//        indexData.setTradingValue(request.tradingValue());
//        indexData.setMarketTotalAmount(request.marketTotalAmount());
////        dataRepository.save(indexData); // dataRepository에 indexData 타입으로 저장
//
//        // dataRepository에 indexData를 저장한다. 이 때 indexData의 id가 자동적으로 생성된다
//        IndexData saved = dataRepository.save(indexData);
//        System.out.println(saved.getIndexInfo().getId());
//        IndexDataDto savedDto = mapper.toDto(saved);
//        System.out.println(savedDto.indexInfoId());
//        return savedDto; // 여기에서 Dto로 바꿨다

        // IndexData 객체를 생성한다
        IndexData indexData = new IndexData();

        // indexData에 데이터를 넣는다
        indexData.setIndexInfo(indexInfo);
        indexData.setBaseDate(request.baseDate());

        indexData.setOpenPrice(request.marketPrice()); // << 시가

        indexData.setClosingPrice(request.closingPrice());
        indexData.setHighPrice(request.highPrice());
        indexData.setLowPrice(request.lowPrice());

        indexData.setChangeValue(request.versus());
        indexData.setFluctuationRate(request.fluctuationRate());
        indexData.setTradingVolume(request.tradingQuantity());
        indexData.setTradingValue(request.tradingPrice());
        indexData.setMarketTotalAmount(request.marketTotalAmount());

        indexData.setSourceType(SourceType.USER);
//        dataRepository.save(indexData); // dataRepository에 indexData 타입으로 저장

        // dataRepository에 indexData를 저장한다. 이 때 indexData의 id가 자동적으로 생성된다
        IndexData saved = dataRepository.save(indexData);
        System.out.println(saved.getIndexInfo().getId());
        IndexDataDto savedDto = mapper.toDto(saved);
        System.out.println(savedDto.indexInfoId());
        return savedDto; // 여기에서 Dto로 바꿨다
    }

//    IndexData의 ID를 repository에서 찾은 다음
//    그 ID에 해당하는 IndexData의 지수, 날짜를 제외한 값을 변경한다
    @Override
    public IndexDataDto updateIndexData(IndexDataUpdateRequest request, Long id) {
        // 만약 id로 indexData를 못찾으면 예외가 뜸
        IndexData indexData = dataRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("IndexData not found!"));

        indexData.setOpenPrice(request.marketPrice()); // <<<

        indexData.setClosingPrice(request.closingPrice());
        indexData.setHighPrice(request.highPrice());
        indexData.setLowPrice(request.lowPrice());
        indexData.setChangeValue(request.versus());
        indexData.setFluctuationRate(request.fluctuationRate());
        indexData.setTradingVolume(request.tradingQuantity());
        indexData.setTradingValue(request.tradingPrice());
        indexData.setMarketTotalAmount(request.marketTotalAmount());

        return mapper.toDto(indexData);
    }

    /**
     * 정렬을 하지 않은 상태
     */
    @Override
    public Page<IndexDataDto> searchByIndexAndDate(IndexDataDateRequest request, Pageable pageable) {
        IndexInfo indexInfo = infoRepository.findById(request.indexInfoId()) // infoRepository에서 일단 id를 찾고
                .orElseThrow(() -> new EntityNotFoundException("IndexInfo not found!"));
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

    /**
     * 지수 데이터 Export
     * 1. 지수 데이터 전체를 Export 하는거니까 List<IndexDataDto> 형태로 전부 찾는다
     * 2. OutputStream 생성
     * 3. CSVWriter를 하나 만든다
     * 4. CSVWriter에 Header - Body 순으로 작성한다
     * CSVWriter.
     */
    @Override
    public byte[] downloadIndexData() {
            // try-catch
            try {
            List<IndexData> listData = dataRepository.findAll();
                // ByteArrayOutputStream을 씀
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                CSVWriter csvWriter = new CSVWriter(
                        new OutputStreamWriter(outputStream, "UTF-8")); // 직렬화, 역직렬화를 할땐 예외 필수

                // CSV header 작성
                String[] header = {"기준일자", "시가", "종가", "고가", "저가", "전일대비등락", "등락률", "거래량", "거래대금", "시가총액"};
                csvWriter.writeNext(header);
                // 0번째 indexData에 있는 기준날짜, 등등을 받아와야됨
                // 반복문을 통해 List<IndexData>를 돌면서 header에 따라 데이터를 넣는다, CSV 작성
                for (int i = 0; i < listData.size(); i++) {
                    IndexData indexData = listData.get(i);
                    String[] csvData = CSVStringMapper.mapper(indexData);
                    csvWriter.writeNext(csvData);
                } // for문 끝
                csvWriter.close();
                return outputStream.toByteArray();
        }catch(Exception e) {
                return null;
        }
    }
}