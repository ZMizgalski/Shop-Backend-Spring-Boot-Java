package com.shop.Main.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ForgotPasswordRequest {

    @NotNull
    private String email;
}
