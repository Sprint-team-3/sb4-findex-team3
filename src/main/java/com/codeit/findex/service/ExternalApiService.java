package com.codeit.findex.service;

import com.codeit.findex.dto.dashboard.OpenApiResponseDto;
import com.codeit.findex.entity.IndexInfo;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ExternalApiService {

  private final RestClient restClient;
  private final String baseUrl;
  private final String apiKey;
  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

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
                    .queryParam("numOfRows", 5)
                    .build())
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(OpenApiResponseDto.class);
  }
}
