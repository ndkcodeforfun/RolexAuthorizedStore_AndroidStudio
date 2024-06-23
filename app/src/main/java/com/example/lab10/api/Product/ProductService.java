package com.example.lab10.api.Product;

import com.example.lab10.model.Category;
import com.example.lab10.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductService {
    final String PRODUCT = "products";
    @GET(PRODUCT)
    Call<List<Product>> getAllProducts();
}
