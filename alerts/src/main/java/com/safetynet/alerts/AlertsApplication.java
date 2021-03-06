package com.safetynet.alerts;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/**
 * Root of Alerts Application *
 */
@SpringBootApplication
@EnableSwagger2
public class AlertsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlertsApplication.class, args);
	}

	/**
	 * on console, give a list of all spring Boot Beans
	 * @param ctx application context
	 * @return commandLineRunner functional interface
	 */
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}

		};
	}

}
