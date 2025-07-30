package com.codeit.findex.dto;

import java.util.List;

public record OpenApiResponseDto(HeadAndBodyDto response) {
  public record HeadAndBodyDto(HeaderDto header, BodyDto body) {}

  public record HeaderDto(String resultCode, String resultMsg) {}

  public record BodyDto(Integer numOfRows, Integer pageNo, Integer totalCount, ItemsDto items) {}

  public record ItemsDto(List<IndexItemDto> item) {}

  public record IndexItemDto(
      String basPntm,
      Double basIdx,
      String basDt,
      String idxCsf,
      String idxNm,
      Integer epyItmsCnt,
      Double clpr,
      Double vs,
      Double fltRt,
      Double mkp,
      Double hipr,
      Double lopr,
      Long trqu,
      Long trPrc,
      Long lstgMrktTotAmt,
      Double lsYrEdVsFltRg,
      Double lsYrEdVsFltRt,
      Double yrWRcrdHgst,
      String yrWRcrdHgstDt,
      Double yrWRcrdLwst,
      String yrWRcrdLwstDt) {}
}
