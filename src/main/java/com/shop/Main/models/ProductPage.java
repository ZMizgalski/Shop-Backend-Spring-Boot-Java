package com.shop.Main.models;

import lombok.Data;

@Data
public class ProductPage {
    private String productName;
    private String description;
    private String productType;
    private int pageNumber = 0;
    private int pageSize = 5;
}
