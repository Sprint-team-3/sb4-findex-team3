package com.example.Findex.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndexData {

  @Id @GeneratedValue private UUID id; // 기본키

  @Column(nullable = false)
  private Instant tradeDate; // 날짜

  @Column private String sourceType; // 소스타입(사용자, OpenAPI)

  @Column private Long openPrice; // 시가

  @Column private Long closePrice; // 종가

  @Column private Long highPrice; // 고가

  @Column private Long changeValue; // 전일 대비 변동 값

  @Column private Long changeRate; // 등락률

  @Column private int tradingVolume; // 거래량

  @Column private int tradingValue; // 거래대금

  @Column private int marketCap; // 상장 시가 총액

  @Column private Instant createdAt; // 데이터 생성 일시
}
