package com.example.lab10.api.order;

import com.example.lab10.api.APIClient;

public class OrderRepository {
    public static OrderService getOrderService(){
        return APIClient.getClient().create(OrderService.class);
    }


}
