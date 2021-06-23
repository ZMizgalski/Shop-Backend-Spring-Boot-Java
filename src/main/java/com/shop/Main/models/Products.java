package com.shop.Main.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Products")
public class Products {

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
//    private String width;
//
//    @NotNull
//    private String height;    Do wysłania mailem
//
//    @NotNull
//    private String depth;
//
//    @NotNull
//    private String materialType;
//
//    @NotNull
//    private String coreOfTheMaterial;
//
//    @NotNull
//     boolean outside;
//
//    @NotNull
//     boolean inside;

    @NotNull
    private String file;
}
