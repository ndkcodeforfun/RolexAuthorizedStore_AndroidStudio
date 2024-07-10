package com.example.lab10.api.Customer;

import com.example.lab10.api.APIClient;
import com.example.lab10.api.ApiResponse;
import com.example.lab10.model.Customer;

import retrofit2.Call;


public class CustomerRepository {
    public static CustomerService getCustomerService() {
        return APIClient.getClient().create(CustomerService.class);
    }

    public static Call<ApiResponse> updateCustomerInfo(int customerId, Customer customer) {
        return getCustomerService().updateCustomerInfo(customerId, customer);
    }
}
