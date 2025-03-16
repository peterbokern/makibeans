package com.makibeans;

import com.makibeans.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final CategoryService categoryService;
    private final AttributeValueService attributeValueService;

    public Application(CategoryService categoryService, AttributeValueService attributeValueService) {

        this.categoryService = categoryService;
        this.attributeValueService = attributeValueService;
    }


    public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Transactional
	@Override
	public void run(String... args) {
       /* AttributeValueRequestDTO attributeValueRequestDTO1 = new AttributeValueRequestDTO(1L, "NL");
        AttributeValueRequestDTO attributeValueRequestDTO2 = new AttributeValueRequestDTO(2L, "EN");
        AttributeValueRequestDTO attributeValueRequestDTO3 = new AttributeValueRequestDTO(3L, "FR");
        attributeValueService.createAttributeValue(attributeValueRequestDTO1);*/
    }

}
