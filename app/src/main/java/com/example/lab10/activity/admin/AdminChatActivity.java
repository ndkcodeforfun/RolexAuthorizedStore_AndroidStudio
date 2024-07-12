package com.example.lab10.activity.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.adapters.ChatAdapter;
import com.example.lab10.api.Message.MessageRepository;
import com.example.lab10.model.MessageDtoRequest;
import com.example.lab10.model.MessageDtoResponse;
import com.example.lab10.model.ChatHistoryResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminChatActivity extends AppCompatActivity {

    private RecyclerView messagesRecyclerView;
    private EditText editTextMessage;
    private Button buttonSend;
    private int customerId;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat);
        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAdminChat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Handle back button press
            }
        });
        messagesRecyclerView = findViewById(R.id.messagesRecyclerViews);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        customerId = getIntent().getIntExtra("CustomerId", -1);

        chatAdapter = new ChatAdapter();
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(chatAdapter);

        loadChatHistory();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void loadChatHistory() {
        MessageRepository.getChatHistoryByCustomerId(customerId).enqueue(new Callback<ChatHistoryResponse>() {
            @Override
            public void onResponse(Call<ChatHistoryResponse> call, Response<ChatHistoryResponse> response) {
                if (response.isSuccessful()) {
                    List<MessageDtoResponse> messages = response.body().getMessageHistory();
                    chatAdapter.setMessages(messages);
                } else {
                    Log.e("AdminChatActivity", "Tải lịch sử chat thất bại (onResponse)");
                }
            }

            @Override
            public void onFailure(Call<ChatHistoryResponse> call, Throwable t) {
                Log.e("AdminChatActivity", "Tải lịch sử chat thất bại (onFailure)", t);
            }
        });
    }

    private void sendMessage() {
        String content = editTextMessage.getText().toString();
        if (content.isEmpty()) return;

        MessageDtoRequest request = new MessageDtoRequest();
        request.setCustomerId(customerId);
        request.setContent(content);
        request.setType("ADMIN");
        request.setSendTime(java.time.LocalDateTime.now().toString()); // Set the send time here

        MessageRepository.getMessageService().sendMessageAdmin(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    editTextMessage.setText("");
                    loadChatHistory(); // Reload chat history to include the new message
                } else {
                    Log.e("AdminChatActivity", "Gửi tin nhắn thất bại (onResponse)");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("AdminChatActivity", "Gửi tin nhắn thất bại (onFailure)", t);
            }
        });
    }
}

