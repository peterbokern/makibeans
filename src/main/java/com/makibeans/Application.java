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
	private final SizeService sizeService;

	public Application(AttributeTemplateService attributeTemplateService, AttributeTemplateRepository attributeTemplateRepository, AttributeValueService attributeValueService, CategoryService categoryService, ProductService productService, ProductAttributeService productAttributeService, SizeService sizeService) {
		this.attributeTemplateService = attributeTemplateService;
        this.attributeValueService = attributeValueService;
        this.categoryService = categoryService;
		this.productService = productService;
        this.productAttributeService = productAttributeService;
		this.sizeService = sizeService;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Transactional
	@Override
	public void run(String... args) throws Exception {

	}




}
