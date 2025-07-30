package com.codeit.findex.entity;

import com.codeit.findex.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "IndexData")
public class IndexData extends BaseEntity {

  /** 이 데이터가 속한 지수 정보 (외래 키) IndexInfo 엔티티와 다대일(N:1) 관계를 맺습니다. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "indexInfoId", nullable = false) // ERD의 'indexInfoId' 컬럼명과 매핑
  private IndexInfo indexInfo;

  /** 기준 날짜 */
  @Column(name = "base_date", nullable = false)
  private LocalDate baseDate;

  /** 소스 타입 (예: USER, OPEN_API) */
  @Column(name = "source_type", length = 20, nullable = false)
  private String sourceType;

  /** 시가 (정규시장 개시 후 최초 가격) */
  @Column(name = "market_price", nullable = false) // ERD의 'market_price' 컬럼명과 매핑
  private double openPrice; // '시가'는 보통 openPrice로 명명합니다.

  /** 종가 (정규시장 종료시 최종 가격) */
  @Column(name = "closing_price", nullable = false)
  private double closingPrice;

  /** 고가 (하루 중 최고치) */
  @Column(name = "high_price", nullable = false)
  private double highPrice;

  /** 저가 (하루 중 최저치) - 일반적인 시세 데이터에 포함되므로 추가 */
  @Column(name = "low_price", nullable = false)
  private double lowPrice;

  /** 전일 대비 변동 값 */
  @Column(name = "versus", nullable = false)
  private double changeValue; // '대비'는 보통 changeValue로 명명합니다.

  /** 등락률 (%) */
  @Column(name = "fluctuation_rate", nullable = false)
  private double fluctuationRate;

  /** 거래량 */
  @Column(name = "trading_quantity", nullable = false)
  private int tradingVolume; // '거래량'은 보통 tradingVolume으로 명명합니다.

  /** 거래대금 */
  @Column(name = "trading_price", nullable = false) // ERD의 'trading_price' 컬럼명과 매핑
  private long tradingValue; // '거래대금'은 보통 tradingValue로 명명하며, 큰 값을 다루므로 Long 타입이 적합합니다.

  /** 상장 시가 총액 */
  @Column(name = "market_total_amount", nullable = false)
  private long marketTotalAmount;

  /** 활성화 여부 */
  @Column(name = "enabled", nullable = false)
  private boolean enabled;
}
