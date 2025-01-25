package com.makibeans;

import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeTemplateRepository;
import com.makibeans.service.AttributeTemplateService;
import com.makibeans.service.AttributeValueService;
import com.makibeans.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application implements CommandLineRunner {


	private final AttributeTemplateService attributeTemplateService;
	private final AttributeValueService attributeValueService;
	private final CategoryService categoryService;

	public Application(AttributeTemplateService attributeTemplateService, AttributeTemplateRepository attributeTemplateRepository, AttributeValueService attributeValueService, CategoryService categoryService) {
		this.attributeTemplateService = attributeTemplateService;
        this.attributeValueService = attributeValueService;
        this.categoryService = categoryService;
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

		categoryService.createRootCategory("Koffie", null, null);
		categoryService.createRootCategory("Koffie", null, null);
		categoryService.createSubCategory("Bonen", null, null, 1L);
		categoryService.createSubCategory("Vieze Bonen", null, null, 2L);






	}


}
