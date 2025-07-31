package com.codeit.findex.service.basic;

import com.codeit.findex.dto.indexInfo.request.IndexInfoCreateRequest;
import com.codeit.findex.dto.indexInfo.request.IndexInfoSearchCond;
import com.codeit.findex.dto.indexInfo.request.IndexInfoUpdateRequest;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoSummaryDto;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.mapper.IndexInfoMapper;
import com.codeit.findex.repository.IndexInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicIndexInfoService {
    private final IndexInfoRepository indexInfoRepository;
    private final IndexInfoMapper indexInfoMapper;

    private List<IndexInfo> findIndexInfoByCondition(IndexInfoSearchCond cond ) {
        String indexName = cond.getIndexName();
        String classification = cond.getIndexClassification();
        Boolean favorite = cond.getFavorite();
        if (indexName !=null && classification != null && favorite != null) {
            return indexInfoRepository.findByIndexNameAndIndexClassificationAndFavorite(indexName, classification, favorite);
        } else if (indexName != null && classification != null) {
            return indexInfoRepository.findByIndexNameAndIndexClassification(indexName, classification);
        } else if (indexName != null && favorite != null) {
            return indexInfoRepository.findByIndexNameAndFavorite(indexName, favorite);
        } else if (classification != null && favorite != null) {
            return indexInfoRepository.findByIndexClassificationAndFavorite(classification, favorite);
        } else if (indexName != null) {
            return indexInfoRepository.findByIndexName(indexName);
        } else if (classification != null) {
            return indexInfoRepository.findByIndexClassification(classification);
        } else if (favorite != null) {
            return indexInfoRepository.findByFavorite(favorite);
        } else {
            return indexInfoRepository.findAll();
        }
    }

    @Transactional
    public IndexInfoDto findIndexInfoById(long id) {
        IndexInfo indexInfo = indexInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Index info with" + id + "not found"));
        return indexInfoMapper.toIndexInfoDto(indexInfo);
    }

    @Transactional
    public List<IndexInfoSummaryDto> findIndexInfoSummaries() {
        return indexInfoRepository.findAll().stream()
                .map(indexInfoMapper::toIndexInfoSummaryDto)
                .toList();
    }

    @Transactional
    public IndexInfoDto registerIndexInfo(IndexInfoCreateRequest dto) {
        if (indexInfoRepository.existsByIndexName(dto.getIndexName())) {
            throw new IllegalArgumentException("Index name" + dto.getIndexName() + " already exists");
        }
        IndexInfo indexInfo = indexInfoMapper.IndexInfoCreateDtoToIndexInfo(dto);
        indexInfoRepository.save(indexInfo);
        return indexInfoMapper.toIndexInfoDto(indexInfo);
    }

    @Transactional
    public IndexInfoDto updateIndexInfo(long id, IndexInfoUpdateRequest dto) {
        IndexInfo indexInfo = indexInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Index info with ID" + id + "not found"));
        if (dto.getEmployedItemsCount() != null) {
            indexInfo.setEmployedItemsCount(dto.getEmployedItemsCount());
        }
        if (dto.getBasePointInTime() != null) {
            indexInfo.setBasepointInTime(dto.getBasePointInTime());
        }
        if (dto.getBaseIndex() != null) {
            indexInfo.setBaseIndex(dto.getBaseIndex());
        }
        if (dto.getFavorite() != null) {
            indexInfo.setFavorite(dto.getFavorite());
        }
        return indexInfoMapper.toIndexInfoDto(indexInfo);
    }

    @Transactional
    public void deleteIndexInfo(long id) {
        IndexInfo indexInfo = indexInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Index info with ID" + id + "not found"));
        indexDataRepository.deleteByIndexInfoId(indexInfo.getId());
        indexInfoRepository.delete(indexInfo);
    }

}
