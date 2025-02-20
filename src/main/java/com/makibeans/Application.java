package com.makibeans;


import com.makibeans.dto.AttributeValueResponseDTO;
import com.makibeans.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private final AttributeValueService attributeValueService;

    public Application(AttributeValueService attributeValueService) {
        this.attributeValueService = attributeValueService;

    }


    public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Transactional
	@Override
	public void run(String... args) {

		AttributeValueResponseDTO responseDTO = attributeValueService.getAttributeValueById(4L);
		System.out.println(responseDTO.getId());
		System.out.println(responseDTO.getTemplateId());
		System.out.println(responseDTO.getValue());

	}




}
