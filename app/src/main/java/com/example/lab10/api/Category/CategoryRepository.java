package com.example.lab10.api.Category;

import com.example.lab10.api.APIClient;

public class CategoryRepository {
    public static CategoryService getCategoryService(){
        return APIClient.getClient().create(CategoryService.class);
    }
}
