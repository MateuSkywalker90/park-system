package com.moliveira.demo_park_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.moliveira.demo_park_api")
public class DemoParkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoParkApiApplication.class, args);
	}

}
