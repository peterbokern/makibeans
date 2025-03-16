package com.makibeans;

import com.makibeans.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class Application implements CommandLineRunner {


    public Application() {}


    public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Transactional
	@Override
	public void run(String... args) {
    }

}
