package com.example.lab10.api;

import com.example.lab10.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryService {
    final String CATEGORY = "categories";
    @GET(CATEGORY)
    Call<List<Category>> getAllCategories();
}
