package com.example.lab10.api.Message;

import com.example.lab10.model.ChatHistoryResponse;
import com.example.lab10.model.MessageDtoRequest;
import com.example.lab10.model.MessageDtoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MessageService {
    @POST("Message")
    Call<Void> sendMessage(@Body MessageDtoRequest messageDtoRequest);

    @GET("Message/history/{CustomerId}")
    Call<ChatHistoryResponse> getChatHistoryByCustomerId(@Path("CustomerId") int customerId);
}
