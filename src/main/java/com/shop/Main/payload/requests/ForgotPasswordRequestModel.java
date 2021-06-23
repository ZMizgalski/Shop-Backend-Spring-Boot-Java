package com.shop.Main.payload.requests;

import com.shop.Main.payload.validators.FieldMatch;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords must be identical")
public class ForgotPasswordRequestModel {

    @NotNull
    private String password;

    @NotNull
    private String confirmPassword;

    @NotNull
    private String token;

    public String getToken() {
        return token;
    }
}
