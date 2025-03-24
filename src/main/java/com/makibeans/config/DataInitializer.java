package com.makibeans.config;

import com.makibeans.dto.*;
import com.makibeans.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component for initializing data in the database.
 * Implements CommandLineRunner to execute code after the application starts.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleService roleService;
    private final UserService userService;
    private final AttributeTemplateService attributeTemplateService;
    private final AttributeValueService attributeValueService;
    private final ProductAttributeService productAttributeService;
    private final ProductService productService;
    private final ProductVariantService productVariantService;
    private final CategoryService categoryService;
    private final SizeService sizeService;

    public DataInitializer(RoleService roleService, UserService userService,
                           AttributeTemplateService attributeTemplateService, AttributeValueService attributeValueService,
                           ProductAttributeService productAttributeService, ProductService productService,
                           ProductVariantService productVariantService, CategoryService categoryService,
                           SizeService sizeService) {
        this.roleService = roleService;
        this.userService = userService;
        this.attributeTemplateService = attributeTemplateService;
        this.attributeValueService = attributeValueService;
        this.productAttributeService = productAttributeService;
        this.productService = productService;
        this.productVariantService = productVariantService;
        this.categoryService = categoryService;
        this.sizeService = sizeService;
    }

    /**
     * Runs the data initialization process.
     *
     * @param args command line arguments
     * @throws Exception if an error occurs during initialization
     */
    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initAttributeTemplates();
        initAttributeValues();
        initCategories();
        initSizes();
        initProducts();
        initProductAttributes();
        initProductVariants();
    }

    private void initRoles() {
        createRoleIfNotExists("ROLE_USER");
        createRoleIfNotExists("ROLE_ADMIN");
        createAdminIfNotExists();
    }

    private void createRoleIfNotExists(String name) {
        if (!roleService.existsByName(name)) {
            roleService.createRole(name);
        }
    }

    private void createAdminIfNotExists() {
        if (!userService.existsByUsername("maki_admin")) {
            userService.registerAdmin(new UserRequestDTO("maki_admin", "maki_admin@makibeans.com", "maki_admin"));
        }
    }

    private void initAttributeTemplates() {
        if (attributeTemplateService.findAll().isEmpty()) {
            List.of(
                    new AttributeTemplateRequestDTO("Origin"),
                    new AttributeTemplateRequestDTO("Flavor"),
                    new AttributeTemplateRequestDTO("Intensity")
            ).forEach(attributeTemplateService::createAttributeTemplate);
        }
    }

    private void initAttributeValues() {
        if (attributeValueService.findAll().isEmpty()) {
            List.of(
                    new AttributeValueRequestDTO(1L, "Colombia"),
                    new AttributeValueRequestDTO(1L, "Ethiopia"),
                    new AttributeValueRequestDTO(1L, "Kenya"),
                    new AttributeValueRequestDTO(1L, "Brazil"),
                    new AttributeValueRequestDTO(2L, "Nutty"),
                    new AttributeValueRequestDTO(2L, "Chocolatey"),
                    new AttributeValueRequestDTO(2L, "Fruity"),
                    new AttributeValueRequestDTO(2L, "Spicy"),
                    new AttributeValueRequestDTO(3L, "Mild"),
                    new AttributeValueRequestDTO(3L, "Medium"),
                    new AttributeValueRequestDTO(3L, "Strong"),
                    new AttributeValueRequestDTO(3L, "Extra Strong")
            ).forEach(attributeValueService::createAttributeValue);
        }
    }

    private void initSizes() {
        if (sizeService.findAll().isEmpty()) {
            List.of(
                    new SizeRequestDTO("100g"),
                    new SizeRequestDTO("250g"),
                    new SizeRequestDTO("500g"),
                    new SizeRequestDTO("Small"),
                    new SizeRequestDTO("Medium"),
                    new SizeRequestDTO("Large")
            ).forEach(sizeService::createSize);
        }
    }

    private void initCategories() {
        if (categoryService.findAll().isEmpty()) {
            List.of(
            new CategoryRequestDTO("Coffee", "All types of coffee beans and blends",
                    "https://fakeurl.com/coffee-beans", null),
                    new CategoryRequestDTO("Brewing Equipment", "Gear for brewing coffee",
                            "https://fakeurl.com/brewing-equipment", null),
                    new CategoryRequestDTO("Accessories", "Accessories for your coffee ritual",
                            "https://fakeurl.com/coffee-accessories", null),

                    new CategoryRequestDTO("Espresso Beans", "Strong, dark-roasted beans perfect for espresso",
                            "https://fakeurl.com/espresso-beans", 1L),
                    new CategoryRequestDTO("Filter Coffee", "Medium-roasted beans for pour-over or drip",
                            "https://fakeurl.com/filter-coffee", 1L),
                    new CategoryRequestDTO("Decaf", "Decaffeinated coffee for late nights",
                            "https://fakeurl.com/decaf-coffee", 1L),

                    new CategoryRequestDTO("Dark Roast", "Deep and bold espresso roast",
                            "https://fakeurl.com/dark-roast", 4L),
                    new CategoryRequestDTO("Medium Roast", "Balanced flavor and smooth finish",
                            "https://fakeurl.com/medium-roast", 4L),
                    new CategoryRequestDTO("Single Origin", "Unique beans from a specific region",
                            "https://fakeurl.com/single-origin", 5L),
                    new CategoryRequestDTO("Blends", "Flavorful blends for daily brews",
                            "https://fakeurl.com/coffee-blends", 5L),

                    new CategoryRequestDTO("French Press", "Immersion brewing gear",
                            "https://fakeurl.com/french-press", 2L),
                    new CategoryRequestDTO("Pour Over", "Tools for manual pour-over brewing",
                            "https://fakeurl.com/pour-over", 2L),
                    new CategoryRequestDTO("Espresso Machines", "Machines for pulling perfect shots",
                            "https://fakeurl.com/espresso-machines", 2L),
                    new CategoryRequestDTO("Manual", "Lever-based espresso machines",
                            "https://fakeurl.com/manual-espresso-machines", 13L),
                    new CategoryRequestDTO("Automatic", "Fully automated espresso brewing",
                            "https://fakeurl.com/automatic-espresso-machines", 13L),

                    new CategoryRequestDTO("Cups & Mugs", "Serve your coffee in style",
                            "https://fakeurl.com/cups-mugs", 3L),
                    new CategoryRequestDTO("Grinders", "Manual and electric grinders",
                            "https://fakeurl.com/coffee-grinders", 3L),
                    new CategoryRequestDTO("Scales", "Precision scales for brewing",
                            "https://fakeurl.com/coffee-scales", 3L)
            ).forEach(categoryService::createCategory);
        }
    }


    private void initProductVariants() {

        if (productVariantService.getAllProductVariants().isEmpty()) {
            List<ProductVariantRequestDTO> productVariants = List.of(
                    new ProductVariantRequestDTO(1L, 1L, 1000L, 20L),
                    new ProductVariantRequestDTO(1L, 2L, 1500L, 15L),
                    new ProductVariantRequestDTO(2L, 1L, 900L, 30L),
                    new ProductVariantRequestDTO(2L, 2L, 1400L, 20L),
                    new ProductVariantRequestDTO(3L, 2L, 1600L, 25L),
                    new ProductVariantRequestDTO(3L, 3L, 2200L, 10L),
                    new ProductVariantRequestDTO(4L, 1L, 950L, 50L),
                    new ProductVariantRequestDTO(4L, 2L, 1300L, 40L),
                    new ProductVariantRequestDTO(5L, 1L, 850L, 60L),
                    new ProductVariantRequestDTO(5L, 2L, 1200L, 35L),
                    new ProductVariantRequestDTO(6L, 4L, 2500L, 15L),
                    new ProductVariantRequestDTO(6L, 5L, 3000L, 10L),
                    new ProductVariantRequestDTO(7L, 4L, 1800L, 25L),
                    new ProductVariantRequestDTO(7L, 5L, 2200L, 15L),
                    new ProductVariantRequestDTO(8L, 6L, 120000L, 5L),
                    new ProductVariantRequestDTO(9L, 5L, 3500L, 20L),
                    new ProductVariantRequestDTO(9L, 6L, 4500L, 12L)
            );

            productVariants.forEach(productVariantService::createProductVariant);
        }
    }

    private void initProducts() {
        if (productService.findAll().isEmpty()) {
            List.of(
                    new ProductRequestDTO("Ethiopian Dark Roast", "Bold and fruity beans from Ethiopia, perfect for espresso lovers.",
                            "https://fakeurl.com/ethiopian-dark-roast", 7L), // Dark Roast

                    new ProductRequestDTO("Colombian Medium Roast", "Balanced, nutty flavor with a smooth finish.",
                            "https://fakeurl.com/colombian-medium-roast", 8L), // Medium Roast

                    new ProductRequestDTO("Single Origin Kenya AA", "Bright and acidic coffee with citrus notes.",
                            "https://fakeurl.com/single-origin-kenya-aa", 9L), // Single Origin

                    new ProductRequestDTO("House Blend Filter", "Smooth and mild blend for everyday pour-over brews.",
                            "https://fakeurl.com/house-blend-filter", 10L), // Blends

                    new ProductRequestDTO("Brazilian Decaf", "Sweet and nutty decaf with no compromise on flavor.",
                            "https://fakeurl.com/brazilian-decaf", 6L), // Decaf

                    new ProductRequestDTO("Bodum French Press", "Classic 8-cup French press made of borosilicate glass.",
                            "https://fakeurl.com/bodum-french-press", 11L), // French Press

                    new ProductRequestDTO("Hario V60 Dripper", "Ceramic pour-over cone for precision brewing.",
                            "https://fakeurl.com/hario-v60-dripper", 12L), // Pour Over

                    new ProductRequestDTO("La Marzocco Linea Mini", "Professional-grade espresso machine for home baristas.",
                            "https://fakeurl.com/la-marzocco-linea-mini", 15L), // Automatic Machine

                    new ProductRequestDTO("Hand Grinder", "Portable manual grinder with ceramic burrs.",
                            "https://fakeurl.com/hand-grinder", 17L), // Grinders

                    new ProductRequestDTO("Coffee Scale", "Digital scale with timer for accurate brewing.",
                            "https://fakeurl.com/coffee-scale", 18L) // Scales
            ).forEach(productService::createProduct);
        }
    }

    private void initProductAttributes() {
        if (productAttributeService.getAllProductAttributes().isEmpty()) {
            for (long productId = 1; productId <= 5; productId++) {
                switch ((int) productId) {
                    case 1 -> { // Ethiopian Dark Roast
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 1L)).getId(), 2L); // Ethiopia
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 2L)).getId(), 7L); // Fruity
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 3L)).getId(), 11L); // Strong
                    }
                    case 2 -> { // Colombian Medium Roast
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 1L)).getId(), 1L); // Colombia
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 2L)).getId(), 5L); // Nutty
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 3L)).getId(), 10L); // Medium
                    }
                    case 3 -> { // Kenya AA
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 1L)).getId(), 3L); // Kenya
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 2L)).getId(), 7L); // Fruity
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 3L)).getId(), 10L); // Medium
                    }
                    case 4 -> { // House Blend
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 1L)).getId(), 4L); // Brazil
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 2L)).getId(), 6L); // Chocolatey
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 3L)).getId(), 9L); // Mild
                    }
                    case 5 -> { // Brazilian Decaf
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 1L)).getId(), 4L); // Brazil
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 2L)).getId(), 5L); // Nutty
                        productAttributeService.addAttributeValue(
                                productAttributeService.createProductAttribute(new ProductAttributeRequestDTO(productId, 3L)).getId(), 9L); // Mild
                    }
                }
            }
        }
    }
}