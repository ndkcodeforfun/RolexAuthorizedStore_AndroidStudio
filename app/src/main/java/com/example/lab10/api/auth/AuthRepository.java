package com.example.lab10.api.auth;

import com.example.lab10.api.APIClient;


public class AuthRepository {
    public static AuthService getAuthService(){
        return APIClient.getClient().create(AuthService.class);
    }
}
