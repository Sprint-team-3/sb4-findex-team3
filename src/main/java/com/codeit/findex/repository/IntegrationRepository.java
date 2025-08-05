package com.codeit.findex.repository;

import com.codeit.findex.entity.Integration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationRepository extends JpaRepository<Integration, Long>, IntegrationCustomRepository {

}
