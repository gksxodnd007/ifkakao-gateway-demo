package com.kakaopay.ifkakao.zuul2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = WebFluxAutoConfiguration.class)
public class Zuul2Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Zuul2Application.class, args);
		Bootstrap bootstrap = context.getBean(Bootstrap.class);
		bootstrap.start();

		Runtime.getRuntime().addShutdownHook(new Thread(bootstrap::stop));
	}
}
