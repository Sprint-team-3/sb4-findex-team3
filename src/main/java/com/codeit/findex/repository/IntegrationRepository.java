package com.codeit.findex.repository;

import com.codeit.findex.entity.Integration;
import com.codeit.findex.entityEnum.JobType;
import com.codeit.findex.entityEnum.Result;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntegrationRepository extends JpaRepository<Integration, Long> {

  @Query(
      """
        SELECT i FROM Integration i
        WHERE (:jobType IS NULL AND i.jobType IN :allJobTypes OR i.jobType = :jobType)
          AND (:worker IS NULL OR i.worker = :worker)
          AND (:result IS NULL OR i.result = :result)
          AND (:indexInfoIds IS NULL OR i.indexInfo.id IN :indexInfoIds)
          AND (:baseDateFrom IS NULL OR :baseDateTo IS NULL OR i.baseDate BETWEEN :baseDateFrom AND :baseDateTo)
    """)
  List<Integration> findByBaseDateConditions(
      @Param("jobType") JobType jobType,
      @Param("allJobTypes") List<JobType> allJobTypes,
      @Param("worker") String worker,
      @Param("result") Result result,
      @Param("indexInfoIds") List<Long> indexInfoIds,
      @Param("baseDateFrom") LocalDate baseDateFrom,
      @Param("baseDateTo") LocalDate baseDateTo,
      Sort sort);

  @Query(
      """
        SELECT i FROM Integration i
        WHERE (:jobType IS NULL AND i.jobType IN :allJobTypes OR i.jobType = :jobType)
          AND (:worker IS NULL OR i.worker = :worker)
          AND (:result IS NULL OR i.result = :result)
          AND (:indexInfoIds IS NULL OR i.indexInfo.id IN :indexInfoIds)
          AND (:jobTimeFrom IS NULL OR :jobTimeTo IS NULL OR i.jobTime BETWEEN :jobTimeFrom AND :jobTimeTo)
    """)
  List<Integration> findByJobTimeConditions(
      @Param("jobType") JobType jobType,
      @Param("allJobTypes") List<JobType> allJobTypes,
      @Param("worker") String worker,
      @Param("result") Result result,
      @Param("indexInfoIds") List<Long> indexInfoIds,
      @Param("jobTimeFrom") LocalDateTime jobTimeFrom,
      @Param("jobTimeTo") LocalDateTime jobTimeTo,
      Sort sort);

  @Query("""
    SELECT i FROM Integration i
    WHERE (:jobType IS NULL AND i.jobType IN :allJobTypes OR i.jobType = :jobType)
      AND (:worker IS NULL OR i.worker = :worker)
      AND (:result IS NULL OR i.result = :result)
      AND (:indexInfoIds IS NULL OR i.indexInfo.id IN :indexInfoIds)
""")
  List<Integration> findByConditionsNoTimeFilter(
      @Param("jobType") JobType jobType,
      @Param("allJobTypes") List<JobType> allJobTypes,
      @Param("worker") String worker,
      @Param("result") Result result,
      @Param("indexInfoIds") List<Long> indexInfoIds,
      Sort sort
  );
}
