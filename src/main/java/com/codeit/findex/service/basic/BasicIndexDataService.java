package com.codeit.findex.service.basic;

import com.codeit.findex.dto.indexData.request.*;
import com.codeit.findex.dto.indexData.response.CursorPageResponseIndexDataDto;
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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicIndexDataService implements IndexDataService {

    private final IndexInfoRepository infoRepository;
    private final IndexDataRepository dataRepository;
    private final IndexDataMapper mapper;
    private final IndexDataRepository indexDataRepository;

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

        IndexDataDto indexDataDto = mapper.toDto(indexData);
        return indexDataDto;
    }

    /**
     * 정렬을 하지 않은 상태
     * 커서 기능을 구현해야한다
     * {소스 타입}을 제외한 모든 속성으로 정렬 및 페이지네이션을 구현합니다.
     * 여러 개의 정렬 조건 중 선택적으로 1개의 정렬 조건만 가질 수 있습니다.
     * 정확한 페이지네이션을 위해 {이전 페이지의 마지막 요소 ID}를 활용합니다.
     * 화면을 고려해 적절한 페이지네이션 전략을 선택합니다.
     */

    @Transactional
    @Override
    public CursorPageResponseIndexDataDto searchByIndexAndDate(Long indexInfoId, String startDate, String endDate, Long idAfter, String cursor, String sortField, String sortDirection, int size) {

        // 1. Create Sort and Pageable objects (this is common for both cases)
        Sort sort = createSort(sortField, sortDirection);
        Pageable pageable = PageRequest.of(0, size, sort);

        // 2. Declare variables for the data slice and total count
        Slice<IndexData> sliceData;
        Long totalElements;

        // 3. Conditional Logic: Branch based on whether indexInfoId is present
        if (indexInfoId != null) {
            // --- CASE 1: indexInfoId IS PROVIDED ---
            // First, validate that the ID exists, just like before.
            if (!infoRepository.existsById(indexInfoId)) {
                throw new EntityNotFoundException("IndexInfo not found with id: " + indexInfoId);
            }

            // Call repository methods that filter by indexInfoId
            sliceData = indexDataRepository.findByConditionsWithCursor(indexInfoId, startDate, endDate, pageable);
            totalElements = indexDataRepository.countByIndexInfoId(indexInfoId);

        } else {
            // --- CASE 2: indexInfoId IS NULL (search all) ---
            // You need repository methods that DON'T filter by indexInfoId.
            // NOTE: You will need to add these methods to your IndexDataRepository.

            // For example:
            sliceData = indexDataRepository.findAllByDateRangeWithCursor(startDate, endDate, pageable);
            totalElements = indexDataRepository.countByDateRange(startDate, endDate);
        }

        // 4. Process the results (this logic remains the same)
        List<IndexData> content = sliceData.getContent();
        List<IndexDataDto> indexDataDto = content.stream()
            .map(mapper::toDto).toList();

        boolean hasNext = sliceData.hasNext();
        Long nextIdAfter = !content.isEmpty() ? content.get(content.size() - 1).getId() : null; // Use null for consistency

        // Your cursor logic seems to depend on the date, which is fine
        String nextcursor = null;
        if (indexDataDto.isEmpty()) {
            nextcursor = startDate;
        }

        return new CursorPageResponseIndexDataDto(indexDataDto, nextcursor, nextIdAfter, size, totalElements, hasNext);

//        IndexInfo indexInfo = infoRepository.findById(indexInfoId) // infoRepository에서 일단 id를 찾고
//        .orElseThrow(() -> new EntityNotFoundException("IndexInfo not found!"));
//
//        // 정렬 객체를 생성하기
//        Sort sort = createSort(sortField, sortDirection);
//
//        // Pageable을 생성하기
//        Pageable pageable = PageRequest.of(0, size, sort);
//        // PageRequest.of(idAfter, size, sort);
//
//        // Repository 호출하기, Slice는 끊어서 보내준다 size가 10이면 10개씩 짧게 짧게 가져와주는 것이다
//        Slice<IndexData> sliceData = indexDataRepository.findByConditionsWithCursor(indexInfoId, startDate, endDate, pageable);
//
//        // Slice<IndexData>를 최종 응답 DTO로 변환하기
//        List<IndexData> content = sliceData.getContent();
//        List<IndexDataDto> indexDataDto = content.stream()
//                .map(mapper::toDto).toList();
//        // 다음에 자료가 더 있는지, 데이터가 더 있는지 true이면 데이터를 더 가져온다
//        boolean hasNext = sliceData.hasNext();
//        // 불러온 데이터의 마지막에 있는 녀석의 id(Long타입)를 가져옴 그 이후의 것들을 불러오기 위한 것
//        Long nextIdAfter = !content.isEmpty() ? content.get(content.size() -1).getId().intValue() : 0L;
//
//        // 커서 값 설정하기, nextCursor는 기준 날짜임
//        String nextcursor = null;
//        if(indexDataDto.isEmpty()) {
//            nextcursor = startDate;
//        }
//
//        // 전체 요소 개수 조회
//        Long totalElements = indexDataRepository.countByIndexInfoId(indexInfoId);
//
//        return new CursorPageResponseIndexDataDto(indexDataDto,nextcursor,nextIdAfter,size,totalElements,hasNext);


    }

    // {소스 타입}을 제외한 모든 속성으로 정렬 및 페이지네이션을 구현합니다.
    private Sort createSort(String sortField, String sortDirection) {
        // 정렬 방향을 결정하는거임
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;

        if(sortField == null) {
            throw new NoSuchElementException("sortField is not found!");
        }
        return Sort.by(direction, sortField).and(Sort.by(Sort.Direction.ASC,"id"));
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