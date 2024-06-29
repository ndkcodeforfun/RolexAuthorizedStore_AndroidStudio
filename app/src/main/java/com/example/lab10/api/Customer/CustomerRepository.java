package com.example.lab10.api.Customer;

import com.example.lab10.api.APIClient;


public class CustomerRepository {
    public static CustomerService getCustomerService() {
        return APIClient.getClient().create(CustomerService.class);
    }
}
