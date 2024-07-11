package com.example.lab10.activity.customer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab10.R;
import com.example.lab10.api.Message.MessageRepository;
import com.example.lab10.model.ChatHistoryResponse;
import com.example.lab10.model.MessageDtoRequest;
import com.example.lab10.model.MessageDtoResponse;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatAdapter chatAdapter;
    private int customerId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        customerId = sharedPreferences.getInt("CustomerId", -1);

        if (customerId != -1) {
            Log.d("ChatFragment", "CustomerId retrieved from SharedPreferences: " + customerId);
            loadChatHistory(customerId);
        } else {
            Toast.makeText(getContext(), "Customer ID not found", Toast.LENGTH_SHORT).show();
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        return view;
    }

    private void loadChatHistory(int customerId) {
        Log.d("ChatFragment", "Loading chat history for CustomerId: " + customerId);
        MessageRepository.getChatHistoryByCustomerId(customerId).enqueue(new Callback<ChatHistoryResponse>() {
            @Override
            public void onResponse(Call<ChatHistoryResponse> call, Response<ChatHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatHistoryResponse chatHistoryResponse = response.body();
                    chatAdapter.setMessages(chatHistoryResponse.getMessageHistory());
                } else {
                    Log.e("ChatFragment", "Failed to load chat history: " + getErrorBody(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<ChatHistoryResponse> call, Throwable t) {
                Log.e("ChatFragment", "Error loading chat history", t);
            }
        });
    }

    private void sendMessage() {
        String content = editTextMessage.getText().toString();
        Log.d("ChatFragment", "Sending message: " + content + " for CustomerId: " + customerId);
        MessageDtoRequest request = new MessageDtoRequest();
        request.setCustomerId(customerId);
        request.setContent(content);
        request.setSendTime(java.time.LocalDateTime.now().toString());

        MessageRepository.sendMessage(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadChatHistory(customerId);
                    editTextMessage.setText("");
                } else {
                    Log.e("ChatFragment", "Failed to send message: " + getErrorBody(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ChatFragment", "Error sending message", t);
            }
        });
    }

    private String getErrorBody(ResponseBody errorBody) {
        try {
            return errorBody != null ? errorBody.string() : "Unknown error";
        } catch (IOException e) {
            return "Error reading error body: " + e.getMessage();
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private List<MessageDtoResponse> messages;

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            MessageDtoResponse message = messages.get(position);
            holder.textViewMessage.setText(message.getContent());
            holder.textViewTime.setText(message.getSendTime());
        }

        @Override
        public int getItemCount() {
            return messages != null ? messages.size() : 0;
        }

        public void setMessages(List<MessageDtoResponse> messages) {
            this.messages = messages;
            notifyDataSetChanged();
        }

        class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView textViewMessage;
            TextView textViewTime;

            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewMessage = itemView.findViewById(R.id.textViewMessage);
                textViewTime = itemView.findViewById(R.id.textViewTime);
            }
        }
    }
}
