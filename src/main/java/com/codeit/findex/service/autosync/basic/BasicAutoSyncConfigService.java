 package com.codeit.findex.service.autosync.basic;

 import com.codeit.findex.dto.autosync.response.AutoSyncConfigDto;
 import com.codeit.findex.entity.IndexInfo;
 import com.codeit.findex.mapper.AutoSyncMapper;
 import com.codeit.findex.repository.IndexInfoRepository;
 import com.codeit.findex.service.autosync.AutoSyncConfigService;
 import jakarta.persistence.EntityNotFoundException;
 import lombok.RequiredArgsConstructor;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;

 @Service
 @RequiredArgsConstructor
 @Transactional
 public class BasicAutoSyncConfigService implements AutoSyncConfigService {
    private final IndexInfoRepository repository;
    private final AutoSyncMapper autoSyncMapper;

    @Override
    public AutoSyncConfigDto updateEnabled(Long id, Boolean enabled) {
        IndexInfo entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("IndexInfo not found: " + id));
        entity.setEnabled(enabled);
        IndexInfo saved = repository.save(entity);
        return autoSyncMapper.toAutoSyncConfigDto(saved);
    }

 }
