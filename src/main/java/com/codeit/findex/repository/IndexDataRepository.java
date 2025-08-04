package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IndexDataRepository extends JpaRepository<IndexData, Long> {

    // 지수 데이터 등록
//    if(dataRepository.exists(request.indexInfo()) && dataRepository.exists(request.baseDate())) {}
    boolean existsByIndexInfoIdAndBaseDate(Long indexInfoId, LocalDate baseDate);

    // 지수 데이터 목록 조회
    Page<IndexData> findByIndexInfoAndBaseDateBetween(IndexInfo indexInfo, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // BasicIntegrationService에서 사용하는 메서드입니다.
    Optional<IndexData> findByIndexInfoIdAndBaseDate(long id, LocalDate localDate);

    // IndexInfoService에서 사용하는 메서드입니다.
    List<IndexData> findAllByIndexInfoId(Long id);
}
