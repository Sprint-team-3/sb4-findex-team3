package com.codeit.findex.service;

import com.codeit.findex.dto.dashboard.OpenApiResponseDto;
import com.codeit.findex.entity.IndexInfo;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternalApiService {

  private final RestClient restClient;
  private final String baseUrl;
  private final String apiKey;

  public ExternalApiService(
      @Value("${external.api.baseurl}") String baseUrl,
      @Value("${external.api.apiKey}") String apiKey) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();

    this.baseUrl = baseUrl;
    this.apiKey = apiKey;

    System.out.println(baseUrl);
    System.out.println(apiKey);
  }

  public OpenApiResponseDto fetchStockMarketIndex() {
    return restClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/getStockMarketIndex")
                    .queryParam("serviceKey", apiKey)
                    .queryParam("resultType", "json")
                    .queryParam("numOfRows", 50)
                    .build())
        .retrieve()
        .body(OpenApiResponseDto.class);
  }

  public List<OpenApiResponseDto.IndexItemDto> fetchIndexData(
      IndexInfo indexInfo, LocalDate from, LocalDate to) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    List<OpenApiResponseDto.IndexItemDto> aggregated = new ArrayList<>();

    for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
      String basDt = date.format(formatter);
      aggregated.addAll(fetchForDate(indexInfo, basDt));
    }

    return aggregated;
  }

  private List<OpenApiResponseDto.IndexItemDto> fetchForDate(IndexInfo indexInfo, String basDt) {
    final int pageSize = 100;
    int pageNo = 1;
    List<OpenApiResponseDto.IndexItemDto> collected = new ArrayList<>();

    while (true) {
      OpenApiResponseDto resp = callIndexApi(indexInfo.getIndexName(), basDt, pageNo, pageSize);

      List<OpenApiResponseDto.IndexItemDto> items = extractItems(resp);
      if (items.isEmpty()) {
        break;
      }

      collected.addAll(items);

      if (items.size() < pageSize) {
        break; // 마지막 페이지
      }
      pageNo++;
    }

    return collected;
  }

  private OpenApiResponseDto callIndexApi(String idxNm, String basDt, int pageNo, int numOfRows) {

    return restClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/getStockMarketIndex")
                    .queryParam("serviceKey", apiKey)
                    .queryParam("resultType", "json")
                    .queryParam("idxNm", idxNm)
                    .queryParam("basDt", basDt)
                    .queryParam("pageNo", pageNo)
                    .queryParam("numOfRows", numOfRows)
                    .build())
        .retrieve()
        .body(OpenApiResponseDto.class);
  }

  private List<OpenApiResponseDto.IndexItemDto> extractItems(OpenApiResponseDto resp) {
    if (resp == null
        || resp.response() == null
        || resp.response().body() == null
        || resp.response().body().items() == null
        || resp.response().body().items().item() == null) {
      return Collections.emptyList();
    }
    return resp.response().body().items().item();
  }
}
