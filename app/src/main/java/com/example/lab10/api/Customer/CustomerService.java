package com.example.lab10.api.Customer;

import com.example.lab10.api.ApiResponse;
import com.example.lab10.model.Customer;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CustomerService {
    @GET("Customer/{id}")
    Call<Customer> getCustomerInfomation(@Path("id") int id);

    @PUT("Customer/{id}")
    Call<ApiResponse> updateCustomerInfo(@Path("id") int id, @Body Customer customer);
}
