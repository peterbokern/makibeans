package com.makibeans;

import com.makibeans.repository.AttributeTemplateRepository;
import com.makibeans.service.AttributeTemplateService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application implements CommandLineRunner {


	private final AttributeTemplateService attributeTemplateService;

	public Application(AttributeTemplateService attributeTemplateService, AttributeTemplateRepository attributeTemplateRepository) {
		this.attributeTemplateService = attributeTemplateService;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


	@Override
	public void run(String... args) throws Exception {


		attributeTemplateService.createAttributeTemplate("Size");


		attributeTemplateService.updateAttributeTemplate(1L, "Origin");

		attributeTemplateService.deleteAttributeTemplate(1L);

		attributeTemplateService.findAll();




	}


}
