package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexInfo;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AutoSyncRepository extends JpaRepository<IndexInfo, Long> {

  /**
   * 커서 기반(keyset) 페이징: 이전 페이지 마지막 요소 ID(lastId) 이후의 레코드만 가져오기
   *
   * @param indexId 조회할 지수 ID (null 허용)
   * @param enabled 활성화 여부 (null 허용)
   * @param lastId 커서—“이전 페이지의 마지막 요소 ID”(null → 첫 페이지)
   * @param limit 한 페이지에 가져올 최대 개수
   */
  @Query(
      """
                  SELECT i
                    FROM IndexInfo i
                   WHERE (:indexId IS NULL   OR i.id      = :indexId)
                     AND (:enabled IS NULL   OR i.enabled = :enabled)
                     AND (:lastId  IS NULL   OR i.id     >  :lastId)
                   ORDER BY i.id ASC
              """)
  List<IndexInfo> findByFilterAfterId(
      @Param("indexId") Long indexId,
      @Param("enabled") Boolean enabled,
      @Param("lastId") Long lastId,
      Pageable limit);

  @Query("""
  SELECT COUNT(i)
  FROM IndexInfo i
  WHERE (:indexId IS NULL OR i.id = :indexId)
    AND (:enabled IS NULL OR i.enabled = :enabled)
""")
  long countByFilter(@Param("indexId") Long indexId, @Param("enabled") Boolean enabled);
}
