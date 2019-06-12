package com.aimeow.Elpida;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ElpidaApplication {
	public static void main(String[] args) {
		SpringApplication.run(ElpidaApplication.class, args);
	}
}
