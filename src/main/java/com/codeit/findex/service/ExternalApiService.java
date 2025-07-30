package com.codeit.findex.service;

import com.codeit.findex.dto.OpenApiResponseDto;
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
                    .build())
        .retrieve()
        .body(OpenApiResponseDto.class);
  }
}
