package com.Rentals.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Rentals App shutting down...");
        }));
		SpringApplication.run(AppApplication.class, args);
	}

}
