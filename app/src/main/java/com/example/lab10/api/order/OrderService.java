package com.example.lab10.api.order;

import com.example.lab10.model.OrderRequestDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrderService {
    @POST("createOrder")
    Call<Void> createOrder(@Body List<OrderRequestDto> order);


}
