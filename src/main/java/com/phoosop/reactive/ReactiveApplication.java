package com.phoosop.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReactiveApplication {

	public static void main(String[] args) {
//		BlockHound.install();
		SpringApplication.run(ReactiveApplication.class, args);
	}

}
