package com.dery.lecatro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // activa o processamento assíncrono (necessário para @Async funcionar)
public class LecatroApplication {

	public static void main(String[] args) {
		SpringApplication.run(LecatroApplication.class, args);
	}

}
