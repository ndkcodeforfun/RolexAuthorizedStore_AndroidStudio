package com.example.lab10.api.Message;

import com.example.lab10.model.MessageDtoRequest;
import com.example.lab10.model.MessageDtoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MessageService {
    @POST("Message")
    Call<Void> sendMessage(@Body MessageDtoRequest messageDtoRequest);

    @GET("Message/history/{customerId}")
    Call<List<MessageDtoResponse>> getChatHistoryByCustomerId(@Path("customerId") int customerId);
}
