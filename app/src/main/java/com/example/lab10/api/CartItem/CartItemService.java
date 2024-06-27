package com.example.lab10.api.CartItem;

import com.example.lab10.model.CartItem;
import com.example.lab10.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartItemService {
    @POST("CartItem")
    Call<Void> AddToCart(@Body CartItem item);

    @GET("CartItem/{CustomerId}")
    Call<List<CartItem>> getCartFromCustomer(@Path("CustomerId") int id);
}
