package com.shop.Main.payload.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProductSearchRequest {

    @NotNull
    private String input;
}
