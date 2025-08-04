package com.codeit.findex.service.autosync.basic;

import com.codeit.findex.dto.autosync.response.AutoSyncConfigDto;
import com.codeit.findex.dto.autosync.response.CursorPageResponseAutoSyncConfigDto;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.mapper.AutoSyncMapper;
import com.codeit.findex.repository.AutoSyncRepository;
import com.codeit.findex.repository.IndexInfoRepository;
import com.codeit.findex.service.autosync.AutoSyncConfigService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicAutoSyncConfigService implements AutoSyncConfigService {
  private final IndexInfoRepository repository;
  private final AutoSyncMapper autoSyncMapper;
  private final AutoSyncRepository autoSyncRepository;

  @Override
  public AutoSyncConfigDto updateEnabled(Long id, Boolean enabled) {
    IndexInfo entity =
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("IndexInfo not found: " + id));
    entity.setEnabled(enabled);
    IndexInfo saved = repository.save(entity);
    return autoSyncMapper.toAutoSyncConfigDto(saved);
  }

  @Override
  public CursorPageResponseAutoSyncConfigDto listAutoSyncConfigs(
      Long indexId, Boolean enabled, Long lastId, int size, String sortBy, String sortDir) {
    // 1) Sort & Pageable 세팅
    Sort.Direction direction = Sort.Direction.fromString(sortDir);
    Pageable pageReq = PageRequest.of(0, size, Sort.by(direction, sortBy));

    // 2) 커서 페이징 조회
    List<IndexInfo> entities =
        autoSyncRepository.findByFilterAfterId(indexId, enabled, lastId, pageReq);

    // 3) 엔티티 → DTO 매핑
    List<AutoSyncConfigDto> content = autoSyncMapper.toAutoSyncConfigDtoList(entities);

    // 4) 다음 커서 계산
    long nextIdAfter = content.isEmpty() ? 0L : content.get(content.size() - 1).id();
    String nextCursor = nextIdAfter != 0L ? Long.toString(nextIdAfter) : null;
    boolean hasNext = content.size() == size;

    // 5) 응답 생성
    return new CursorPageResponseAutoSyncConfigDto(
        content, nextCursor, nextIdAfter, size, content.size(), hasNext);
  }
}
