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

    // 조건 추가 생략 (기존 그대로)

    if (jobType != null) {
      query.where(integration.jobType.eq(jobType));
    }

    if (indexInfoId != null) {
      query.where(integration.indexInfo.id.eq(indexInfoId));
    }

    if (baseDateFrom != null) {
      query.where(integration.targetDate.goe(baseDateFrom));
    }

    if (baseDateTo != null) {
      query.where(integration.targetDate.loe(baseDateTo));
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

      if ("targetDate".equals(sortField)) {
        var sortPath = entityPath.getComparable("targetDate", LocalDate.class);
        LocalDate cursorDate = cursor.toLocalDate();
        if ("asc".equalsIgnoreCase(sortDirection)) {
          query.where(sortPath.gt(cursorDate));
        } else {
          query.where(sortPath.lt(cursorDate));
        }
      } else if ("jobTime".equals(sortField)) {
        var sortPath = entityPath.getComparable("jobTime", LocalDateTime.class);
        if ("asc".equalsIgnoreCase(sortDirection)) {
          query.where(sortPath.gt(cursor));
        } else {
          query.where(sortPath.lt(cursor));
        }
      } else {
        throw new IllegalArgumentException("Invalid sortField: " + sortField);
      }
    }

    // 정렬 추가
    Order order = "asc".equalsIgnoreCase(sortDirection) ? Order.ASC : Order.DESC;
    PathBuilder<Integration> entityPath = new PathBuilder<>(Integration.class, "integration");
    OrderSpecifier<?> orderSpecifier;
    if ("targetDate".equals(sortField)) {
      orderSpecifier = new OrderSpecifier<>(order, entityPath.getComparable("targetDate", LocalDate.class));
    } else if ("jobTime".equals(sortField)) {
      orderSpecifier = new OrderSpecifier<>(order, entityPath.getComparable("jobTime", LocalDateTime.class));
    } else {
      throw new IllegalArgumentException("Invalid sortField: " + sortField);
    }
    query.orderBy(orderSpecifier);

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
      query.where(integration.targetDate.goe(baseDateFrom));
    }

    if (baseDateTo != null) {
      query.where(integration.targetDate.loe(baseDateTo));
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
