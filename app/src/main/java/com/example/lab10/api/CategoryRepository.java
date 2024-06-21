package com.example.lab10.api;

public class CategoryRepository {
    public static CategoryService getCategoryService(){
        return APIClient.getClient().create(CategoryService.class);
    }
}
