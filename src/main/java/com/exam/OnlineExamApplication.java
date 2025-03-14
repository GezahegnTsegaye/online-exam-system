package com.exam;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Exam Management API", version = "1.0"))
public class OnlineExamApplication {
	public static void main(String[] args) {
		SpringApplication.run(OnlineExamApplication.class, args);
	}
}
