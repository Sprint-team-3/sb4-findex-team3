package com.codeit.findex.service.basic;

import com.codeit.findex.dto.IndexDataDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.mapper.file.IndexDataMapper;
import com.codeit.findex.repository.IndexDataRepository;
import com.codeit.findex.repository.IndexInfoRepository;
import com.codeit.findex.request.IndexDataDateRequest;
import com.codeit.findex.request.IndexDataSearchRequest;
import com.codeit.findex.service.IndexDataService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BasicIndexDataService implements IndexDataService {

    private final IndexInfoRepository infoRepository;
    private final IndexDataRepository dataRepository;
    private final IndexDataMapper mapper;

    @Override
    public Page<IndexDataDto> searchByIndexAndDate(IndexDataDateRequest request, org.springframework.data.domain.Pageable pageable) {
        IndexInfo indexInfo = infoRepository.findById(request.indexInfo())
                .orElseThrow(() -> new EntityNotFoundException("IndexInfo를 찾을수가 없어요!"));
        Page<IndexData> searchData = dataRepository.findByIndexInfoAndBaseDateBetween(indexInfo, request.startDate(), request.endDate(), pageable);
        return searchData.map(mapper::toDto);
    }

    @Override
    public IndexDataDto searchIndexData(IndexDataSearchRequest request) {
        IndexData indexData = null;
        if(dataRepository.findById(request.id()).equals(infoRepository.findById(request.id()))) {
            indexData = dataRepository.findById(request.id()).get();
        }
        return mapper.toDto(indexData);
    }
}
