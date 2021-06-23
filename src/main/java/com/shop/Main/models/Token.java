package com.shop.Main.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

@Data
@Document(collection = "Tokens")
public class Token {

    @NotNull
    private String email;

    @NotNull
    private boolean isUsed;

    @NotNull
    private Date expiryDate;

    @NotNull
    private String token;

    public void setExpiryDate(int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        this.expiryDate = now.getTime();
    }

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
}
