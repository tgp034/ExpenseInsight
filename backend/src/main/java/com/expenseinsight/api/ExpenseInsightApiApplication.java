package com.expenseinsight.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the ExpenseInsight Spring Boot application.
 *
 * <p>This class bootstraps the backend services, enabling REST controllers,
 * repositories, and other Spring-managed components defined in the project.</p>
 */
@SpringBootApplication
public class ExpenseInsightApiApplication {

	/**
     * Launches the ExpenseInsight application.
     *
     * @param args command-line arguments passed to the application
     */
	public static void main(String[] args) {
		SpringApplication.run(ExpenseInsightApiApplication.class, args);
	}

}
