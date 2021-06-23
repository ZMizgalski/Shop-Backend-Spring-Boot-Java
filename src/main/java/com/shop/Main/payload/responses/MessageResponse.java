package com.shop.Main.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class MessageResponse {

    @NotNull
    private String message;
}
