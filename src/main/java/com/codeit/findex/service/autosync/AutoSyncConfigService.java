package com.codeit.findex.service.autosync;

import com.codeit.findex.dto.autosync.response.AutoSyncConfigDto;
import com.codeit.findex.dto.autosync.response.CursorPageResponseAutoSyncConfigDto;

public interface AutoSyncConfigService {

  /**
   * 활성화(enabled) 속성만 업데이트합니다. 트랜잭션(@Transactional) 내에서 동작하며, 기존 엔티티 조회 후 enabled 값 변경, 저장, DTO 반환
   * 과정을 수행합니다.
   *
   * @param id 업데이트할 엔티티의 ID
   * @param enabled 새 활성화 상태
   * @return 업데이트된 설정 정보 DTO
   */
  AutoSyncConfigDto updateEnabled(Long id, Boolean enabled);

  /**
   * 자동 연동 설정 목록 조회
   *
   * @param indexId (optional) 지수 ID 필터
   * @param enabled (optional) 활성화 여부 필터
   * @param lastId (optional) 이전 페이지 마지막 요소 ID (커서)
   * @param size (optional) 한 페이지 크기
   * @param sortBy (optional) 정렬 컬럼
   * @param sortDir (optional) 정렬 방향
   * @return 커서 기반 페이지 응답 DTO
   */
  CursorPageResponseAutoSyncConfigDto listAutoSyncConfigs(
      Long indexId, Boolean enabled, Long lastId, int size, String sortBy, String sortDir);
}
