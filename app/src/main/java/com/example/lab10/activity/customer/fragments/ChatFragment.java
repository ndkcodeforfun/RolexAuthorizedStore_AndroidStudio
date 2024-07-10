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
import com.example.lab10.activity.auth.JWTUtils;
import com.example.lab10.api.Message.MessageRepository;
import com.example.lab10.model.MessageDtoRequest;
import com.example.lab10.model.MessageDtoResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

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
        String accessToken = sharedPreferences.getString("accessToken", null);
        if (accessToken != null) {
            try {
                String[] decodedParts = JWTUtils.decoded(accessToken);
                String body = decodedParts[1];
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());
                loadChatHistory(customerId);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to decode token", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Access token not found", Toast.LENGTH_SHORT).show();
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
        MessageRepository.getChatHistoryByCustomerId(customerId).enqueue(new Callback<List<MessageDtoResponse>>() {
            @Override
            public void onResponse(Call<List<MessageDtoResponse>> call, Response<List<MessageDtoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chatAdapter.setMessages(response.body());
                } else {
                    try {
                        Log.e("ChatFragment", "Failed to load chat history: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Failed to load chat history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MessageDtoResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load chat history: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ChatFragment", "Failed to load chat history", t);
            }
        });
    }

    private void sendMessage() {
        String content = editTextMessage.getText().toString();
        if (content.isEmpty()) {
            Toast.makeText(getContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

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
                    try {
                        Log.e("ChatFragment", "Failed to send message: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to send message: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ChatFragment", "Failed to send message", t);
            }
        });
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
