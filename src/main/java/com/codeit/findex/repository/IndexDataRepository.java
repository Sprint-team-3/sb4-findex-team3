package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexData;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexDataRepository extends JpaRepository<IndexData, UUID> {

  Optional<IndexData> findByIndexInfoId(long uuid);

  List<IndexData> findAllByIndexInfoId(long id);
}
