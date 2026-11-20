package com.moliveira.demo_park_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.TimeZone;

@SpringBootApplication
@ComponentScan(basePackages = "com.moliveira.demo_park_api")
public class DemoParkApiApplication {

	public static final String TIMEZONE = "America/Manaus";

	public static void main(String[] args) {
		// Set before Spring starts, so the database pool and every timestamp
		// conversion use the same zone (setting it later shifted stored times).
		TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE));
		SpringApplication.run(DemoParkApiApplication.class, args);
	}

}
