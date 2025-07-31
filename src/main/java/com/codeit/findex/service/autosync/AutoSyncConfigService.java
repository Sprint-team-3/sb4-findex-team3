package com.codeit.findex.service.autosync;

import com.codeit.findex.dto.autosync.response.AutoSyncConfigDto;

public interface AutoSyncConfigService {

    /**
     * 활성화(enabled) 속성만 업데이트합니다.
     * 트랜잭션(@Transactional) 내에서 동작하며,
     * 기존 엔티티 조회 후 enabled 값 변경, 저장, DTO 반환 과정을 수행합니다.
     * @param id 업데이트할 엔티티의 ID
     * @param enabled 새 활성화 상태
     * @return 업데이트된 설정 정보 DTO
     */
    AutoSyncConfigDto updateEnabled(Long id, Boolean enabled);
}
