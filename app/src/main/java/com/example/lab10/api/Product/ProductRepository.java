package com.example.lab10.api.Product;

import com.example.lab10.api.APIClient;

public class ProductRepository {
    public static ProductService getProductService(){
        return APIClient.getClient().create(ProductService.class);
    }
}
