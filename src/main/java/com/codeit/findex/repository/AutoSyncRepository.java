package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoSyncRepository extends JpaRepository<IndexInfo, Long> {}
