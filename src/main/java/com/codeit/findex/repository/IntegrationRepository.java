package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entity.Integration;
import com.codeit.findex.entityEnum.JobType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntegrationRepository
    extends JpaRepository<Integration, Long>, IntegrationCustomRepository {

  @Query("""
    SELECT i FROM Integration i
    WHERE i.jobType = :jobType
      AND i.worker = :worker
      AND i.result = com.codeit.findex.entityEnum.Result.SUCCESS
      AND i.jobTime > :afterTime
""")
  List<Integration> findRecentSuccessLogs(
      @Param("jobType") JobType jobType,
      @Param("worker") String worker,
      @Param("afterTime") LocalDateTime afterTime
  );

  @Query("""
SELECT i FROM Integration i
WHERE i.indexInfo.id = :indexInfoId
  AND i.jobType = :jobType
  AND i.worker = :worker
  AND i.result = com.codeit.findex.entityEnum.Result.SUCCESS
  AND i.jobTime > :afterTime
""")
  List<Integration> findRecentLogs(
      @Param("indexInfoId") Long indexInfoId,
      @Param("jobType") JobType jobType,
      @Param("worker") String worker,
      @Param("afterTime") LocalDateTime afterTime);

}
