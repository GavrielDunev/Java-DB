package com.example.xml_processing.model.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "categories")
@XmlAccessorType(XmlAccessType.FIELD)
public class CategoriesWithProductRootDto {

    @XmlElement(name = "category")
    private List<CategoriesWithProductDto> categories;

    public List<CategoriesWithProductDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoriesWithProductDto> categories) {
        this.categories = categories;
    }
}
