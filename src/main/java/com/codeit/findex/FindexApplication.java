package com.codeit.findex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling // 스케쥴링 사용
@SpringBootApplication
public class FindexApplication {

  public static void main(String[] args) {

    SpringApplication.run(FindexApplication.class, args);
  }
}
