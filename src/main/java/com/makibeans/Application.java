package com.makibeans;

import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.model.Product;
import com.makibeans.model.ProductAttribute;
import com.makibeans.repository.AttributeTemplateRepository;
import com.makibeans.service.*;
import org.hibernate.Hibernate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
public class Application implements CommandLineRunner {


	private final AttributeTemplateService attributeTemplateService;
	private final AttributeValueService attributeValueService;
	private final CategoryService categoryService;
	private final ProductService productService;
	private final ProductAttributeService productAttributeService;

	public Application(AttributeTemplateService attributeTemplateService, AttributeTemplateRepository attributeTemplateRepository, AttributeValueService attributeValueService, CategoryService categoryService, ProductService productService, ProductAttributeService productAttributeService) {
		this.attributeTemplateService = attributeTemplateService;
        this.attributeValueService = attributeValueService;
        this.categoryService = categoryService;
		this.productService = productService;
        this.productAttributeService = productAttributeService;
    }

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Transactional
	@Override
	public void run(String... args) throws Exception {


		/*AttributeTemplate attributeTemplate = attributeTemplateService.createAttributeTemplate("Size");

		attributeTemplateService.updateAttributeTemplate(1L, "Origin");

		//attributeTemplateService.deleteAttributeTemplate(1L);

		attributeTemplateService.findAll();

		AttributeValue attributeValue = attributeValueService.createAttributeValue(1L, "Something");

		attributeValueService.updateAttributeValue(1L, "Something Else");

		attributeValueService.deleteAttributeValue(1L);

		categoryService.createRootCategory("Koffie", null, null);

		categoryService.createSubCategory("Bonen", null, null, 1L);
		categoryService.createSubCategory("Vieze Bonen", null, null, 2L);*/




		//categoryService.updateCategory(15L, "new", null, null, 22L);

		//AttributeTemplate attributeTemplate = attributeTemplateService.createAttributeTemplate("Size");

		//Product product = productService.createProduct("productName", "description", 23L, "url");
		//ProductAttribute productAttribute = productAttributeService.createProductAttribute(1L, 1L);


		for (Product p: productService.findAll()) {
			for (ProductAttribute pa : p.getProductAttributes()) {
				System.out.println(pa);
			}
		}

		productAttributeService.createProductAttribute(100L, 100L);
	}


}
