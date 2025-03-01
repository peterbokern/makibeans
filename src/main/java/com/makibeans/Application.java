package com.makibeans;


import com.makibeans.dto.AttributeValueResponseDTO;
import com.makibeans.dto.CategoryRequestDTO;
import com.makibeans.dto.CategoryResponseDTO;
import com.makibeans.exeptions.CircularReferenceException;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.model.Category;
import com.makibeans.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final CategoryService categoryService;

    public Application(AttributeValueService attributeValueService, CategoryService categoryService) {

        this.categoryService = categoryService;
    }


    public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Transactional
	@Override
	public void run(String... args) {}

}
