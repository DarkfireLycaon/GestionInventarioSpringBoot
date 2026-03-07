package com.galvan.inventarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InventariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventariosApplication.class, args);

        
	}

}
