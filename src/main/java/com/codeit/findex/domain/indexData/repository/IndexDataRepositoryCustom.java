package com.codeit.findex.domain.indexData.repository;

import com.codeit.findex.domain.base.entity.IndexData;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

public interface IndexDataRepositoryCustom {
    Slice<IndexData> findSlice(
            Long indexInfoId,
            LocalDate startDate,
            LocalDate endDate,
            Long idAfter, // 마지막 항목의 id
            int size,
            Sort sort
    );

    Long countAll(Long indexInfoId, LocalDate startDate, LocalDate endDate);
}
