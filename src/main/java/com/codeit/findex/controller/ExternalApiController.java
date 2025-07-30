package com.codeit.findex.controller;

import com.codeit.findex.dto.dashboard.OpenApiResponseDto;
import com.codeit.findex.dto.dashboard.OpenApiResponseDto.*;
import com.codeit.findex.service.ExternalApiService;
import java.util.List;
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
    OpenApiResponseDto openApiResponseDto =  externalApiService.fetchStockMarketIndex();

    // 사용 예시
    HeadAndBodyDto headAndBodyDto = openApiResponseDto.response();
    BodyDto bodyDto = headAndBodyDto.body();
    ItemsDto items = bodyDto.items();
    List<IndexItemDto> indexItemDtos = items.item();
    String basPntm = indexItemDtos.get(0).basPntm();

    return null;
  }

}
