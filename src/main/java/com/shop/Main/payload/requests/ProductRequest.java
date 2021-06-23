package com.shop.Main.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ProductRequest {

    @NotNull
    @Id
    private String id;

    @NotNull
    private String productName;

    @NotNull
    private String description;

    @NotNull
    private String productType;

    @NotNull
    private String productOriginPage;

//    @NotNull
//    private String color;
//
//    @NotNull
//    private String width;
//
//    @NotNull
//    private String height;
//
//    @NotNull
//    private String depth;
//
//    @NotNull
//    private String materialType;
//
//    @NotNull
//    private String coreOfTheMaterial;

    @NotNull
    private boolean outside;

    @NotNull
    private boolean inside;

    @NotNull
    private String file;
}
