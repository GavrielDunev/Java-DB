package com.example.xml_processing.model.dto;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsersAndProductsDto {

    @XmlAttribute(name = "first-name")
    private String firstName;
    @XmlAttribute(name = "last-name")
    private String lastName;
    @XmlAttribute(name = "age")
    private Integer age;
    @XmlElement(name = "sold-products")
    private List<ProductNameAndPriceDto> products;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<ProductNameAndPriceDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductNameAndPriceDto> products) {
        this.products = products;
    }
}
