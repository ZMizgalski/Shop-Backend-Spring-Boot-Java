package com.shop.Main.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class LoginResponse {

    @NotNull
    private String id;

    @NotNull
    private String name;

    @NotNull
    private String email;
}
