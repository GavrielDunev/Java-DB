package com.example.xml_processing;

import com.example.xml_processing.model.dto.*;
import com.example.xml_processing.service.*;
import com.example.xml_processing.util.XmlParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    private static final String RESOURCE_FILES_DIRECTORY = "src/main/resources/files/";
    private static final String CATEGORIES_FILE_NAME = "categories.xml";
    private static final String USERS_FILE_NAME = "users.xml";
    private static final String PRODUCTS_FILE_NAME = "products.xml";
    private static final String OUTPUT_DIRECTORY_PATH = "src/main/resources/files/output/";
    private static final String PRODUCTS_IN_RANGE = "products-in-range.xml";
    private static final String USERS_SOLD_PRODUCTS = "users-sold-products.xml";
    private static final String CATEGORIES_BY_PRODUCTS = "categories-by-products.xml";
    private static final String USERS_AND_PRODUCTS = "users-and-products.xml";
    private static final String ORDERED_CUSTOMERS = "ordered-customers.xml";
    private static final String TOYOTA_CARS = "toyota-cars.xml";
    private static final String LOCAL_SUPPLIERS = "local-suppliers.xml";
    private static final String CARS_AND_PARTS = "cars-and-parts.xml";
    private static final String CUSTOMERS_TOTAL_SALES = "customers-total-sales.xml";
    private static final String SALES_DISCOUNTS = "sales-discounts.xml";

    private final CategoryService categoryService;
    private final BufferedReader bufferedReader;
    private final XmlParser xmlParser;
    private final UserService userService;
    private final ProductService productService;

    public CommandLineRunnerImpl(CategoryService categoryService, XmlParser xmlParser, UserService userService, ProductService productService) {
        this.categoryService = categoryService;
        this.xmlParser = xmlParser;
        this.userService = userService;
        this.productService = productService;
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run(String... args) throws Exception {
        seedData();

        System.out.println("Enter exercise number:");
        int ex = Integer.parseInt(bufferedReader.readLine());

        switch (ex) {
            case 1 -> productsInRangeWithNoBuyer();
            case 2 -> successfullySoldProducts();
            case 3 -> categoriesByProductsCount();
            case 4 -> usersAndProducts();
            case 5 -> orderedCustomers();
            case 6 -> carsFromMakeToyota();
            case 7 -> localSuppliers();
            case 8 -> carsWithTheirListOfParts();
            case 9 -> totalSalesByCustomer();
            case 10 -> salesWithAppliedDiscount();
        }
    }

    private void salesWithAppliedDiscount() throws IOException {

    }

    private void totalSalesByCustomer() throws IOException {

    }

    private void carsWithTheirListOfParts() throws IOException {

    }

    private void localSuppliers() throws IOException {

    }

    private void carsFromMakeToyota() throws IOException {

    }

    private void orderedCustomers() throws IOException {

    }

    private void usersAndProducts() throws IOException, JAXBException {
        UsersAndProductsRootDto rootUser = this.userService.findAllWithMoreThanOneSoldProduct();

        xmlParser.writeToFile(OUTPUT_DIRECTORY_PATH + USERS_AND_PRODUCTS,
                rootUser);
    }

    private void categoriesByProductsCount() throws IOException, JAXBException {
       CategoriesWithProductRootDto categoriesRootDto = this.categoryService.findAllByProductCount();

       xmlParser.writeToFile(OUTPUT_DIRECTORY_PATH + CATEGORIES_BY_PRODUCTS,
               categoriesRootDto);
    }

    private void successfullySoldProducts() throws IOException, JAXBException {
        UserSoldProductsRootDto user = this.userService.findAllWithSoldProductsAndBuyer();

        xmlParser.writeToFile(OUTPUT_DIRECTORY_PATH + USERS_SOLD_PRODUCTS,
                user);
    }

    private void productsInRangeWithNoBuyer() throws IOException, JAXBException {
        ProductViewRootDto product = this.productService.findAllByPriceRange();

        xmlParser.writeToFile(OUTPUT_DIRECTORY_PATH + PRODUCTS_IN_RANGE,
                product);
    }

    private void seedData() throws IOException, JAXBException {
        if (this.categoryService.getCountOfCategories() == 0) {
            CategoriesSeedRootDto categoriesSeedRootDto = xmlParser.fromFile(RESOURCE_FILES_DIRECTORY + CATEGORIES_FILE_NAME,
                    CategoriesSeedRootDto.class);
            this.categoryService.seedCategories(categoriesSeedRootDto.getCategories());
        }

        if (this.userService.getCountOfUsers() == 0) {
            UserSeedRootDto userSeedRootDto = xmlParser.fromFile(RESOURCE_FILES_DIRECTORY + USERS_FILE_NAME,
                    UserSeedRootDto.class);
            this.userService.seedUsers(userSeedRootDto.getUsers());
        }

        if (this.productService.getCount() == 0) {
            ProductSeedRootDto productSeedRootDto = xmlParser.fromFile(RESOURCE_FILES_DIRECTORY + PRODUCTS_FILE_NAME,
                    ProductSeedRootDto.class);
            this.productService.seedProducts(productSeedRootDto.getProducts());
        }

    }
}