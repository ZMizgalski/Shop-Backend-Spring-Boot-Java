package com.shop.Main.payload.requests;

import lombok.Data;

import java.util.Map;

@Data
public class MailRequest {

    private String from;
    private String to;
    private String subject;
    private Map<String, Object> model;

}
