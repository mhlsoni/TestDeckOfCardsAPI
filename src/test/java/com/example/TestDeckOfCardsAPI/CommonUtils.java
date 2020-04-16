package com.example.TestDeckOfCardsAPI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class CommonUtils {

    public static HttpHeaders getHttpHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
