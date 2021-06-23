package com.shop.Main.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@AllArgsConstructor
@Data
public class JwtResponse {

    private String token;
    private final String type = "Bearer";
    private String id;
    private String username;
    private String email;
    private List<String> roles;
}
