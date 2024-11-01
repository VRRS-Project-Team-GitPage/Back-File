package com.shinhan.VRRS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class VrrsApplication {
	public static void main(String[] args) {
		SpringApplication.run(VrrsApplication.class, args);
	}
}