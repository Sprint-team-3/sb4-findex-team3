package com.codeit.findex.service;

import com.codeit.findex.dto.indexInfo.request.IndexInfoCreateRequest;
import com.codeit.findex.dto.indexInfo.request.IndexInfoSearchCond;
import com.codeit.findex.dto.indexInfo.request.IndexInfoUpdateRequest;
import com.codeit.findex.dto.indexInfo.response.CursorPageResponseIndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoSummaryDto;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.mapper.IndexInfoMapper;
import com.codeit.findex.repository.IndexInfoRepository;
import com.codeit.findex.repository.IndexInfoRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IndexInfoService {
    private final IndexInfoRepository indexInfoRepository;
    private final IndexInfoRepositoryCustom indexInfoRepositoryCustom;
    private final IndexInfoMapper indexInfoMapper;

    @Transactional
    public CursorPageResponseIndexInfoDto findIndexInfoByCondition(IndexInfoSearchCond cond ) {
        List<IndexInfo> indexInfos;
    }

    @Transactional
    public IndexInfoDto findIndexInfoById(UUID id) {
        IndexInfo indexInfo = indexInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Index info with" + id + "not found"));
        return indexInfoMapper.toIndexInfoDto(indexInfo);
    }

    @Transactional
    public IndexInfoSummaryDto findIndexInfoSummaries() {
        List<IndexInfo> indexInfos = indexInfoRepository.findAll();

    }

    @Transactional
    public IndexInfoDto registerIndexInfo(IndexInfoCreateRequest request) {
    }

    @Transactional
    public IndexInfoDto updateIndexInfo(IndexInfoUpdateRequest request) {}

    @Transactional
    public void deleteIndexInfo(UUID id) {}

}
