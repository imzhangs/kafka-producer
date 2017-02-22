package com.kd;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KafkaProducerServiceApp {
	
	
	@Value("${send.kfk.threadpool.size}")
	int poolSize;
	
	@Bean
	public ExecutorService sendKfkExcutor(){
		ExecutorService executor = Executors.newFixedThreadPool(poolSize);
		return executor;
	}
	
	
	public static void main(String[] args) {
		SpringApplication.run(KafkaProducerServiceApp.class, args);
	}
	
}
