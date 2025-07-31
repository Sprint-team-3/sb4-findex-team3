package com.codeit.findex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FindexApplication {

  public static void main(String[] args) {

    SpringApplication.run(FindexApplication.class, args);
  }
}
