package com.codeit.findex.dto.indexInfo.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  private LocalDate timestamp;
  private int status;
  private String message;
  private String details;
}
