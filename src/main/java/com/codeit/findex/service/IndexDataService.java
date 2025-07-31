package com.codeit.findex.service;

import com.codeit.findex.dto.IndexDataDto;
import com.codeit.findex.request.IndexDataDateRequest;
import com.codeit.findex.request.IndexDataSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IndexDataService {

    // 지수, 날짜로 지수 데이터 목록을 조회하는 메서드
    Page<IndexDataDto> searchByIndexAndDate(IndexDataDateRequest request, Pageable pageable);

    // 지수 정보의 id를 통해 지수 데이터를 가져오는 메서드, 팀장님 오더
    IndexDataDto searchIndexData(IndexDataSearchRequest request);

}
