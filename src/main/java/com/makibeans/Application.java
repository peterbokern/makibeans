package com.makibeans;

import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeTemplateRepository;
import com.makibeans.service.AttributeTemplateService;
import com.makibeans.service.AttributeValueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application implements CommandLineRunner {


	private final AttributeTemplateService attributeTemplateService;
	private final AttributeValueService attributeValueService;

	public Application(AttributeTemplateService attributeTemplateService, AttributeTemplateRepository attributeTemplateRepository, AttributeValueService attributeValueService) {
		this.attributeTemplateService = attributeTemplateService;
        this.attributeValueService = attributeValueService;
    }

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


	@Override
	public void run(String... args) throws Exception {


		AttributeTemplate attributeTemplate = attributeTemplateService.createAttributeTemplate("Size");

		attributeTemplateService.updateAttributeTemplate(1L, "Origin");

		//attributeTemplateService.deleteAttributeTemplate(1L);

		attributeTemplateService.findAll();

		AttributeValue attributeValue = attributeValueService.createAttributeValue(1L, "Something");

		attributeValueService.updateAttributeValue(1L, "Something Else");

		attributeValueService.deleteAttributeValue(1L);

	}


}
