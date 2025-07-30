package com.codeit.findex.entity;

import com.codeit.findex.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Integration")
public class Integration extends BaseEntity {

  /**
   * 작업 대상이 된 지수 정보 (외래 키)
   * IndexInfo 엔티티와 다대일(N:1) 관계를 맺습니다.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "indexInfoId") // ERD의 'indexInfoId' 컬럼명과 매핑
  private IndexInfo indexInfo;

  /**
   * 작업 대상이 된 특정 지수 데이터 (외래 키, 선택적)
   * IndexData 엔티티와 다대일(N:1) 관계를 맺습니다.
   * '지수 정보' 연동 시에는 이 필드가 null일 수 있습니다.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "data_id")
  private IndexData indexData;

  /**
   * 연동 작업의 유형을 나타내는 Enum
   */
  public enum JobType {
    INDEX_INFO, // 지수 정보 연동
    INDEX_DATA  // 지수 데이터 연동
  }

  /**
   * 연동 작업의 유형 (지수 정보, 지수 데이터)
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "job_type", length = 100, nullable = false)
  private JobType jobType;

  /**
   * 연동 대상 날짜 범위의 시작일
   */
  @Column(name = "base_date_from")
  private LocalDate baseDateFrom;

  /**
   * 연동 대상 날짜 범위의 종료일
   */
  @Column(name = "base_date_to")
  private LocalDate baseDateTo;

  /**
   * 작업을 실행한 주체 (사용자 IP 또는 "system")
   */
  @Column(name = "worker", length = 100, nullable = false)
  private String worker;

  /**
   * 작업 실행 시간 범위의 시작 시간 (조회용)
   */
  @Column(name = "job_time_from")
  private LocalDateTime jobTimeFrom;

  /**
   * 작업 실행 시간 범위의 종료 시간 (조회용)
   */
  @Column(name = "job_time_to")
  private LocalDateTime jobTimeTo;

  /**
   * 작업 결과 (성공/실패)
   */
  @Column(name = "result", nullable = false)
  private boolean result;
}
