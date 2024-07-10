package com.example.lab10.api.Message;

import com.example.lab10.api.APIClient;
import com.example.lab10.model.MessageDtoRequest;
import com.example.lab10.model.MessageDtoResponse;

import java.util.List;

import retrofit2.Call;

public class MessageRepository {
    private static MessageService messageService;

    public static MessageService getMessageService() {
        if (messageService == null) {
            messageService = APIClient.getClient().create(MessageService.class);
        }
        return messageService;
    }

    public static Call<Void> sendMessage(MessageDtoRequest message) {
        return getMessageService().sendMessage(message);
    }

    public static Call<List<MessageDtoResponse>> getChatHistoryByCustomerId(int customerId) {
        return getMessageService().getChatHistoryByCustomerId(customerId);
    }
}
