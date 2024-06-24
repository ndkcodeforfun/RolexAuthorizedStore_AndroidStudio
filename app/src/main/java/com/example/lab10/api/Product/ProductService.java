package com.example.lab10.api.Product;

import com.example.lab10.model.Category;
import com.example.lab10.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductService {

    @GET("products")
    Call<List<Product>> getAllProducts();

    @GET("product/{id}")
    Call<Product> find(@Path("id") int id);
    @POST("Products/CreateProduct")
    Call<Void> create(@Body Product product);

    @PUT("product/{id}")
    Call<Void> update(@Body Product product, @Path("id") int id);

    @PUT("product/setStatus/{id}")
    Call<Void> setStatus(@Path("id") int id);
}
