package com.shop.Main.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
public class JwtUserData {

    @NotNull
    private String token;
}
