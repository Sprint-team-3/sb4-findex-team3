package com.codeit.findex.repository;

import com.codeit.findex.entity.Integration;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationRepository extends JpaRepository<Integration, UUID> {}
