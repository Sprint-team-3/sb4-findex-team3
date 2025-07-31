package com.codeit.findex.entity;

import com.codeit.findex.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="IndexInfo")
public class IndexInfo extends BaseEntity {

//  @OneToMany(mappedBy = "IndexInfo", cascade = CascadeType.ALL, orphanRemoval = true)
//  private List<IndexData> indexDataList;

  /**
   * 지수의 분류명
   */
  @Column(name = "index_classification", length = 100, nullable = false)
  private String indexClassification;

  /**
   * 지수의 분류명 (예: 코스피 200)
   */
  @Column(name = "index_name", length = 255, nullable = false, unique = true)
  private String indexName;

  /**
   * 해당 지수를 구성하는 종목의 총 개수
   */
  @Column(name = "employed_items_count")
  private int employedItemsCount;

  /**
   * 지수 산출의 기준이 되는 날짜 및 시간
   */
  @Column(name = "basepoint_intime", nullable = false)
  private LocalDate basepointInTime;

  /**
   * 기준 시점의 지수 값 (보통 100 또는 1000)
   */
  @Column(name = "base_index", nullable = false)
  private double baseIndex;

  /**
   * 정보가 입력된 출처 (예: "USER", "OPEN_API")
   */
  @Column(name = "source_type", length = 20, nullable = false)
  private String sourceType;

  /**
   * 사용자의 즐겨찾기 여부
   */
  @Column(name = "favorite", nullable = false)
  private boolean favorite;

  /**
   * 활성화 여부 (예: 자동 연동 설정의 활성/비활성 상태)
   */
  @Column(name = "enabled", nullable = false)
  private boolean enabled;
}
