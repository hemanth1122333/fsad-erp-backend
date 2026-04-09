package com.education.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Educational ERP Spring Boot application.
 * @SpringBootApplication acts as a combination of @Configuration, 
 * @EnableAutoConfiguration, and @ComponentScan.
 */
@SpringBootApplication
public class ErpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpApplication.class, args);
        System.out.println("Educational ERP Backend is running!...");
    }

}
