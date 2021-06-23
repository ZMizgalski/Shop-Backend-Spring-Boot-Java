package com.shop.Main.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ProductSearchResponse {

    @NotNull
    private String id;

    @NotNull
    private String productName;

    @NotNull
    private String description;

    @NotNull
    private String productType;

    @NotNull
    private String file;
}
