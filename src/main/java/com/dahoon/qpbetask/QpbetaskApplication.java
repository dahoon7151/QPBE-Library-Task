package com.dahoon.qpbetask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class QpbetaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(QpbetaskApplication.class, args);
	}

}
