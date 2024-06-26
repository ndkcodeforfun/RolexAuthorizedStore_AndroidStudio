package com.example.lab10.api.CartItem;

import com.example.lab10.api.APIClient;
import com.example.lab10.api.Category.CategoryService;

public class CartItemRepository {
    public static CartItemService getCartItemService(){
        return APIClient.getClient().create(CartItemService.class);
    }
}
