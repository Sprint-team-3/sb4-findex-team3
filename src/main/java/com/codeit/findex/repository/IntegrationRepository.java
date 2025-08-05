package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entity.Integration;
import com.codeit.findex.entityEnum.JobType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationRepository extends JpaRepository<Integration, Long>, IntegrationCustomRepository {

  Optional<Integration> findTopByIndexInfoAndJobTypeAndWorkerOrderByJobTimeDesc(IndexInfo indexInfo, JobType jobType, String workerIp);

  Optional<Integration> findTopByIndexInfoAndIndexDataAndJobTypeAndWorkerOrderByJobTimeDesc(
      IndexInfo indexInfo, IndexData indexData,
      JobType jobType, String workerIp);
}
