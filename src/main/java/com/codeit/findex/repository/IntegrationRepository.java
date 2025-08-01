package com.codeit.findex.repository;

import com.codeit.findex.entity.Integration;
import com.codeit.findex.entityEnum.JobType;
import com.codeit.findex.entityEnum.Result;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationRepository extends JpaRepository<Integration, Long> {
  @Query(
      """
  SELECT i FROM Integration i
  WHERE (:jobType IS NULL OR i.jobType = :jobType)
    AND (:indexInfoIds IS NULL OR i.indexInfo.id IN :indexInfoIds)
    AND (:baseDateFrom IS NULL OR i.baseDate >= :baseDateFrom)
    AND (:baseDateTo IS NULL OR i.baseDate <= :baseDateTo)
    AND (:worker IS NULL OR i.worker = :worker)
    AND (:status IS NULL OR i.result = :status)
    AND (:jobTimeFrom IS NULL OR i.jobTime >= :jobTimeFrom)
    AND (:jobTimeTo IS NULL OR i.jobTime <= :jobTimeTo)
    AND (:idAfter IS NULL OR i.id > :idAfter)
  ORDER BY
    CASE WHEN :sortField = 'jobTime' AND :sortDirection = 'ASC' THEN i.jobTime END ASC,
    CASE WHEN :sortField = 'jobTime' AND :sortDirection = 'DESC' THEN i.jobTime END DESC,
    CASE WHEN :sortField = 'id' AND :sortDirection = 'ASC' THEN i.id END ASC,
    CASE WHEN :sortField = 'id' AND :sortDirection = 'DESC' THEN i.id END DESC
""")
  List<Integration> findByConditionsWithCursor(
      @Param("jobType") JobType jobType,
      @Param("indexInfoIds") List<Long> indexInfoIds,
      @Param("baseDateFrom") LocalDate baseDateFrom,
      @Param("baseDateTo") LocalDate baseDateTo,
      @Param("worker") String worker,
      @Param("status") Result status,
      @Param("jobTimeFrom") LocalDateTime jobTimeFrom,
      @Param("jobTimeTo") LocalDateTime jobTimeTo,
      @Param("idAfter") Long idAfter,
      @Param("sortField") String sortField,
      @Param("sortDirection") String sortDirection,
      Pageable pageable);

  @Query(
      """
  SELECT COUNT(i) FROM Integration i
  WHERE (:jobType IS NULL OR i.jobType = :jobType)
    AND (:indexInfoIds IS NULL OR i.indexInfo.id IN :indexInfoIds)
    AND (:baseDateFrom IS NULL OR i.baseDate >= :baseDateFrom)
    AND (:baseDateTo IS NULL OR i.baseDate <= :baseDateTo)
    AND (:worker IS NULL OR i.worker = :worker)
    AND (:status IS NULL OR i.result = :status)
    AND (:jobTimeFrom IS NULL OR i.jobTime >= :jobTimeFrom)
    AND (:jobTimeTo IS NULL OR i.jobTime <= :jobTimeTo)
""")
  long countByConditions(
      @Param("jobType") JobType jobType,
      @Param("indexInfoIds") List<Long> indexInfoIds,
      @Param("baseDateFrom") LocalDate baseDateFrom,
      @Param("baseDateTo") LocalDate baseDateTo,
      @Param("worker") String worker,
      @Param("status") Result status,
      @Param("jobTimeFrom") LocalDateTime jobTimeFrom,
      @Param("jobTimeTo") LocalDateTime jobTimeTo);
}
