package com.example.lab10.api.Message;

import com.example.lab10.api.APIClient;
import com.example.lab10.model.ChatHistoryResponse;
import com.example.lab10.model.MessageDtoRequest;

import retrofit2.Call;

public class MessageRepository {
    public static MessageService getMessageService() {
        return APIClient.getClient().create(MessageService.class);
    }

    public static Call<ChatHistoryResponse> getChatHistoryByCustomerId(int customerId) {
        return getMessageService().getChatHistoryByCustomerId(customerId);
    }

    public static Call<Void> sendMessage(MessageDtoRequest messageDtoRequest) {
        return getMessageService().sendMessage(messageDtoRequest);
    }

    public static Call<Void> sendMessageAdmin(MessageDtoRequest messageDtoRequest) {
        return getMessageService().sendMessageAdmin(messageDtoRequest);
    }
}
