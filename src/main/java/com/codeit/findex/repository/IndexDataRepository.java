package com.codeit.findex.repository;

import com.codeit.findex.dto.IndexDataDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface IndexDataRepository extends JpaRepository<IndexData, UUID> {

    // 지수 데이터 등록
//    if(dataRepository.exists(request.indexInfo()) && dataRepository.exists(request.baseDate())) {}
    boolean existsByIndexInfoAndBaseDate(IndexInfo indexInfo, LocalDate baseDate);

    // 지수 데이터 목록 조회
    Page<IndexData> findByIndexInfoAndBaseDateBetween(IndexInfo indexInfo, LocalDate startDate, LocalDate endDate, Pageable pageable);

//    IndexData findByIndexInfo(UUID id);
//    지수 정보의 id를 통해 지수 데이터를 가져오는 메서드, 팀장님 오더
}
