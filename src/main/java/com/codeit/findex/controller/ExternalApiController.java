package com.codeit.findex.controller;

import com.codeit.findex.dto.OpenApiResponseDto;
import com.codeit.findex.service.ExternalApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external")
public class ExternalApiController {

  private final ExternalApiService externalApiService;

  public ExternalApiController(ExternalApiService externalApiService) {
    this.externalApiService = externalApiService;
  }

  @GetMapping
  public OpenApiResponseDto getStockMarketIndex() {
    return externalApiService.fetchStockMarketIndex();
  }

}
