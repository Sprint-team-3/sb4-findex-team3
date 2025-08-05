package com.codeit.findex.repository.custom;

import com.codeit.findex.entity.Integration;
import com.codeit.findex.entity.QIntegration;
import com.codeit.findex.entityEnum.JobType;
import com.codeit.findex.entityEnum.Result;
import com.codeit.findex.repository.IntegrationCustomRepository;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class IntegrationCustomRepositoryImpl implements IntegrationCustomRepository {

  private final JPAQueryFactory queryFactory;

  public IntegrationCustomRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Slice<Integration> searchIntegrations(
      JobType jobType,
      Long indexInfoId,
      LocalDate baseDateFrom,
      LocalDate baseDateTo,
      String worker,
      LocalDateTime jobTimeFrom,
      LocalDateTime jobTimeTo,
      Result status,
      LocalDateTime cursor,
      String sortField,
      String sortDirection,
      Pageable pageable) {

    QIntegration integration = QIntegration.integration;

    var query = queryFactory.selectFrom(integration);

    // 조건 추가
    if (jobType != null) {
      query.where(integration.jobType.eq(jobType));
    }

    if (indexInfoId != null) {
      query.where(integration.indexInfo.id.eq(indexInfoId));
    }

    if (baseDateFrom != null) {
      query.where(integration.baseDate.goe(baseDateFrom));
    }

    if (baseDateTo != null) {
      query.where(integration.baseDate.loe(baseDateTo));
    }

    if (worker != null && !worker.isEmpty()) {
      query.where(integration.worker.eq(worker));
    }

    if (jobTimeFrom != null) {
      query.where(integration.jobTime.goe(jobTimeFrom));
    }

    if (jobTimeTo != null) {
      query.where(integration.jobTime.loe(jobTimeTo));
    }

    if (status != null) {
      query.where(integration.result.eq(status));
    }

    // 커서 기반 페이징 조건
    if (cursor != null) {
      PathBuilder<Integration> entityPath = new PathBuilder<>(Integration.class, "integration");
      var sortPath = entityPath.getComparable(sortField, Comparable.class);

      // 커서 정렬 기준과 방향에 따라 조건 추가
      if ("asc".equalsIgnoreCase(sortDirection)) {
        query.where(sortPath.gt(cursor));
      } else {
        query.where(sortPath.lt(cursor));
      }
    }

    // 정렬 추가
    Order order = "asc".equalsIgnoreCase(sortDirection) ? Order.ASC : Order.DESC;
    query.orderBy(
        new OrderSpecifier<>(
            order,
            new PathBuilder<>(Integration.class, "integration")
                .getComparable(sortField, Comparable.class)));

    // 페이징
    query.offset(pageable.getOffset());
    query.limit(pageable.getPageSize() + 1);

    // 결과 조회
    List<Integration> results = query.fetch();

    // Slice 객체 생성 (다음 페이지 여부 계산)
    boolean hasNext = results.size() > pageable.getPageSize();

    if (hasNext) {
      results.remove(results.size() - 1);
    }

    return new SliceImpl<>(results, pageable, hasNext);
  }

  @Override
  public long countIntegrations(
      JobType jobType,
      Long indexInfoId,
      LocalDate baseDateFrom,
      LocalDate baseDateTo,
      String worker,
      LocalDateTime jobTimeFrom,
      LocalDateTime jobTimeTo,
      Result status) {

    QIntegration integration = QIntegration.integration;

    var query = queryFactory.selectFrom(integration);

    if (jobType != null) {
      query.where(integration.jobType.eq(jobType));
    }

    if (indexInfoId != null) {
      query.where(integration.indexInfo.id.eq(indexInfoId));
    }

    if (baseDateFrom != null) {
      query.where(integration.baseDate.goe(baseDateFrom));
    }

    if (baseDateTo != null) {
      query.where(integration.baseDate.loe(baseDateTo));
    }

    if (worker != null && !worker.isEmpty()) {
      query.where(integration.worker.eq(worker));
    }

    if (jobTimeFrom != null) {
      query.where(integration.jobTime.goe(jobTimeFrom));
    }

    if (jobTimeTo != null) {
      query.where(integration.jobTime.loe(jobTimeTo));
    }

    if (status != null) {
      query.where(integration.result.eq(status));
    }

    return query.select(integration.count()).fetchOne();
  }
}
