package com.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableBatchProcessing
@SpringBootApplication(scanBasePackages = {"com.spring.batch"})
public class SpringBatchReadDbWriteFileApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchReadDbWriteFileApplication.class, args);
	}

}
