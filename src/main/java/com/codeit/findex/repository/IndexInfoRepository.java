package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IndexInfoRepository extends JpaRepository<IndexInfo, UUID> {
}
