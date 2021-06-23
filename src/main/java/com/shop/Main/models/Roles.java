package com.shop.Main.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Data
@Document(collection = "Roles")
public class Roles {

    @NotNull
    private String id;

    @NotNull
    private ERoles name;
}
