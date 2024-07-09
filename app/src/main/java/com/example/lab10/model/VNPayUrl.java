package com.example.lab10.model;

import java.io.Serializable;

public class VNPayUrl implements Serializable {
    private String url;
    public VNPayUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
