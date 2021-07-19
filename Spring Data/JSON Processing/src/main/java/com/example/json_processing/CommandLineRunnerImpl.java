package com.example.json_processing;

import com.example.json_processing.model.dto.*;
import com.example.json_processing.service.*;
import com.google.gson.Gson;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    private static final String OUTPUT_DIRECTORY_PATH = "src/main/resources/files/output/";
    private static final String PRODUCTS_IN_RANGE = "products-in-range.json";
    private static final String USERS_SOLD_PRODUCTS = "users-sold-products.json";
    private static final String CATEGORIES_BY_PRODUCTS = "categories-by-products.json";
    private static final String USERS_AND_PRODUCTS = "users-and-products.json";
    private static final String ORDERED_CUSTOMERS = "ordered-customers.json";
    private static final String TOYOTA_CARS = "toyota-cars.json";
    private static final String LOCAL_SUPPLIERS = "local-suppliers.json";
    private static final String CARS_AND_PARTS = "cars-and-parts.json";
    private static final String CUSTOMERS_TOTAL_SALES = "customers-total-sales.json";
    private static final String SALES_DISCOUNTS = "sales-discounts.json";

    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final BufferedReader bufferedReader;
    private final Gson gson;
    private final SupplierService supplierService;
    private final PartService partService;
    private final CarService carService;
    private final CustomerService customerService;
    private final SaleService saleService;

    public CommandLineRunnerImpl(UserService userService, CategoryService categoryService, ProductService productService, Gson gson, SupplierService supplierService, PartService partService, CarService carService, CustomerService customerService, SaleService saleService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.gson = gson;
        this.supplierService = supplierService;
        this.partService = partService;
        this.carService = carService;
        this.customerService = customerService;
        this.saleService = saleService;
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
       List<SalesFullInfoDto> salesFullInfoDtos = this.saleService.findAllSales();

        String content = gson.toJson(salesFullInfoDtos);

        writeToFile(OUTPUT_DIRECTORY_PATH + SALES_DISCOUNTS, content);
    }

    private void totalSalesByCustomer() throws IOException {
       List<CustomerTotalSalesInfoDto> customerTotalSalesDtos = this.customerService.findAllWithBoughtCar();

        String content = gson.toJson(customerTotalSalesDtos);

        writeToFile(OUTPUT_DIRECTORY_PATH + CUSTOMERS_TOTAL_SALES, content);
    }

    private void carsWithTheirListOfParts() throws IOException {
        List<CarsAndPartsInfoDto> carsAndPartsDtos = this.carService.findAll();

        String content = gson.toJson(carsAndPartsDtos);

        writeToFile(OUTPUT_DIRECTORY_PATH + CARS_AND_PARTS, content);
    }

    private void localSuppliers() throws IOException {
        List<LocalSuppliersInfoDto> localSuppliersDtos = this.supplierService.findAllNotImporters();

        String content = gson.toJson(localSuppliersDtos);

        writeToFile(OUTPUT_DIRECTORY_PATH + LOCAL_SUPPLIERS, content);
    }

    private void carsFromMakeToyota() throws IOException {
        List<CarsFromMakeInfoDto> carsFromMakeInfoDtos = this.carService.findAllByMakeToyotaOrderByModel();

        String content = gson.toJson(carsFromMakeInfoDtos);

        writeToFile(OUTPUT_DIRECTORY_PATH + TOYOTA_CARS, content);
    }

    private void orderedCustomers() throws IOException {
        List<OrderedCustomersInfoDto> orderedCustomersDtos = this.customerService
                .findAllOrderedByBirthDate();

        String content = gson.toJson(orderedCustomersDtos);

        writeToFile(OUTPUT_DIRECTORY_PATH + ORDERED_CUSTOMERS, content);
    }

    private void usersAndProducts() throws IOException {
        UsersAndUsersCountDto usersAndProductsDto = this.userService
                .findAllWithSoldProductsOrderBySoldProductsCount();

        String content = gson.toJson(usersAndProductsDto);
        writeToFile(OUTPUT_DIRECTORY_PATH + USERS_AND_PRODUCTS, content);
    }

    private void categoriesByProductsCount() throws IOException {
        List<CategoriesByProductsDto> categories = this.categoryService
                .getAllOrderedByProducts();

        writeToFile(OUTPUT_DIRECTORY_PATH + CATEGORIES_BY_PRODUCTS, gson.toJson(categories));
    }

    private void successfullySoldProducts() throws IOException {
        List<UsersSoldProductsDto> userSoldProductsDtos = this.userService
                .findAllWithSoldProductsOrderByLastAndFirstName();
        writeToFile(OUTPUT_DIRECTORY_PATH + USERS_SOLD_PRODUCTS, gson.toJson(userSoldProductsDtos));
    }

    private void productsInRangeWithNoBuyer() throws IOException {
        List<ProductNameAndPriceDto> dtos = this.productService
                .findProductsInRange(BigDecimal.valueOf(500L), BigDecimal.valueOf(1000L));

        String content = gson.toJson(dtos);
        writeToFile(OUTPUT_DIRECTORY_PATH + PRODUCTS_IN_RANGE, content);
    }

    private void writeToFile(String path, String content) throws IOException {
        Files.write(Path.of(path), Collections.singleton(content));
    }

    private void seedData() throws IOException {
       this.userService.seedUsers();
       this.categoryService.seedCategories();
       this.productService.seedProducts();
       this.supplierService.seedSuppliers();
       this.partService.seedParts();
       this.carService.seedCars();
       this.customerService.seedCustomers();
       this.saleService.seedSales();
    }
}